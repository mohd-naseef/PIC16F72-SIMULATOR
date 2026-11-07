import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/** PIC16F72 teaching UI with an assembly-driven Hamming(12,8) Week 5 demonstration. */
public class SimulatorFrame extends JFrame {
    private final JTextArea source = new JTextArea();
    private final JTextArea trace = new JTextArea();
    private final JTextArea errors = new JTextArea();
    private final DefaultTableModel ramModel = new DefaultTableModel(new Object[]{"Address","Hex","Decimal","Binary"},0) {
        public boolean isCellEditable(int r,int c){ return false; }
    };
    private final JTable ramTable = new JTable(ramModel);
    private final JLabel wLabel=new JLabel("W: 00"), pcLabel=new JLabel("PC: 0"), spLabel=new JLabel("SP: 0"),
        flagsLabel=new JLabel("Z:0  C:0  DC:0"), haltLabel=new JLabel("Halted: No");
    private final DefaultListModel<String> fifoModel=new DefaultListModel<>();
    private final JList<String> fifoList=new JList<>(fifoModel);
    private final int[] ram=new int[256];
    private final ArrayDeque<Integer> fifo=new ArrayDeque<>();
    private java.util.List<String> program=new ArrayList<>();
    private final Map<String,Integer> labels=new HashMap<>();
    private int pc=0,w=0,sp=0,status=0;
    private boolean halted=false;
    private javax.swing.Timer runTimer;
    private JButton runButton;

    private static final String EXAMPLE =
"; WEEK 5 - HAMMING (12,8) SINGLE-BIT CORRECTION\n"+
"; Data byte is stored at RAM[0x20]. Encoded 12-bit word at 0x30/0x31.\n"+
"; HENC and HDEC are simulator teaching instructions that perform\n"+
"; the Hamming algorithm; all control remains in the assembly workflow.\n"+
"        MOVLW 0x86       ; Example data = 134\n"+
"        MOVWF 0x20       ; Store original data\n"+
"        HENC 0x20,0x30   ; Create 12-bit Hamming code\n"+
"        FLIP 0x30,2      ; Inject one error at codeword bit 2 (position 3)\n"+
"        HDEC 0x20,0x30   ; Detect syndrome and correct one bit\n"+
"        SLEEP            ; Finish demonstration\n";

    public SimulatorFrame(){
        super("PIC16F72 Educational Microcontroller Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900,620));
        setSize(1360,820); setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        source.setFont(new Font(Font.MONOSPACED,Font.PLAIN,12)); source.setLineWrap(true); source.setWrapStyleWord(false); source.setText(EXAMPLE); source.setCaretPosition(0);
        trace.setEditable(false); errors.setEditable(false);
        trace.setFont(new Font(Font.MONOSPACED,Font.PLAIN,12)); errors.setFont(new Font(Font.MONOSPACED,Font.PLAIN,12));
        buildUI(); refresh();
        runTimer=new javax.swing.Timer(500,e->step());
    }

    private void buildUI(){
        setLayout(new BorderLayout(4,4));
        JLabel header=new JLabel("  PIC16F72  |  EDUCATIONAL MICROCONTROLLER SIMULATOR  |  WEEK 5: HAMMING ECC");
        header.setOpaque(true); header.setBackground(new Color(30,55,78)); header.setForeground(Color.WHITE);
        header.setFont(new Font("SansSerif",Font.BOLD,15)); header.setBorder(BorderFactory.createEmptyBorder(10,8,10,8));
        add(header,BorderLayout.NORTH);

        // Left: editable assembly and compact two-row controls. Line wrapping prevents
        // the horizontal scroll position from hiding the first characters on laptops.
        JPanel left=new JPanel(new BorderLayout(3,3));
        left.setBorder(BorderFactory.createTitledBorder("PROGRAM MEMORY (editable assembly)"));
        JScrollPane sourceScroll=new JScrollPane(source,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        left.add(sourceScroll,BorderLayout.CENTER);
        JPanel buttons=new JPanel(new GridLayout(2,4,5,5));
        JButton load=new JButton("Load"), step=new JButton("Step");
        runButton=new JButton("Run"); JButton run=runButton, reset=new JButton("Reset");
        JButton inject=new JButton("Inject Error"), enqueue=new JButton("Enqueue"), dequeue=new JButton("Dequeue");
        buttons.add(load); buttons.add(step); buttons.add(run); buttons.add(reset);
        buttons.add(inject); buttons.add(enqueue); buttons.add(dequeue); buttons.add(new JLabel(""));
        for(Component c:buttons.getComponents()) if(c instanceof JButton){
            JButton b=(JButton)c; b.setMargin(new Insets(5,4,5,4)); b.setFont(new Font("SansSerif",Font.PLAIN,12));
        }
        left.add(buttons,BorderLayout.SOUTH);
        left.setMinimumSize(new Dimension(300,250));

        JPanel right=new JPanel(new BorderLayout(3,3));
        ramTable.setFont(new Font(Font.MONOSPACED,Font.PLAIN,12)); ramTable.setRowHeight(21);
        ramTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JPanel mem=new JPanel(new BorderLayout()); mem.setBorder(BorderFactory.createTitledBorder("DATA MEMORY (RAM)"));
        mem.add(new JScrollPane(ramTable),BorderLayout.CENTER);
        JPanel tracePanel=new JPanel(new BorderLayout()); tracePanel.setBorder(BorderFactory.createTitledBorder("EXECUTION TRACE"));
        tracePanel.add(new JScrollPane(trace),BorderLayout.CENTER);
        tracePanel.setMinimumSize(new Dimension(210,180));
        JSplitPane memoryTrace=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,mem,tracePanel);
        memoryTrace.setResizeWeight(.62); memoryTrace.setContinuousLayout(true); memoryTrace.setOneTouchExpandable(true);
        memoryTrace.setDividerSize(9); memoryTrace.setBorder(null);

        JPanel errorPanel=new JPanel(new BorderLayout()); errorPanel.setBorder(BorderFactory.createTitledBorder("ERRORS (FROM LOGGER)"));
        errorPanel.add(new JScrollPane(errors),BorderLayout.CENTER);
        JSplitPane rightStack=new JSplitPane(JSplitPane.VERTICAL_SPLIT,memoryTrace,errorPanel);
        rightStack.setResizeWeight(.78); rightStack.setContinuousLayout(true); rightStack.setOneTouchExpandable(true);
        rightStack.setDividerSize(9); rightStack.setBorder(null);
        right.add(rightStack,BorderLayout.CENTER);

        JSplitPane workArea=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,left,right);
        workArea.setResizeWeight(.34); workArea.setContinuousLayout(true); workArea.setOneTouchExpandable(true);
        workArea.setDividerSize(10); workArea.setBorder(null);
        add(workArea,BorderLayout.CENTER);

        JPanel bottom=new JPanel(new BorderLayout(6,3));
        JPanel cpu=new JPanel(new GridLayout(2,3,8,2)); cpu.setBorder(BorderFactory.createTitledBorder("CPU STATUS"));
        JLabel pcTitle=pcLabel, spTitle=spLabel;
        for(JLabel label:new JLabel[]{wLabel,pcTitle,spTitle,flagsLabel,haltLabel,new JLabel("PIC16F72  |  Week 5")}){
            label.setFont(new Font("SansSerif",Font.BOLD,12)); cpu.add(label);
        }
        JPanel fifoPanel=new JPanel(new BorderLayout()); fifoPanel.setBorder(BorderFactory.createTitledBorder("FIFO QUEUE"));
        fifoPanel.add(new JScrollPane(fifoList),BorderLayout.CENTER); fifoPanel.setPreferredSize(new Dimension(190,82));
        bottom.add(cpu,BorderLayout.CENTER); bottom.add(fifoPanel,BorderLayout.EAST);
        bottom.setPreferredSize(new Dimension(0,105));
        add(bottom,BorderLayout.SOUTH);

        load.addActionListener(e->loadProgram()); step.addActionListener(e->step());
        run.addActionListener(e->{if(runTimer.isRunning()){runTimer.stop();run.setText("Run");}else{runTimer.start();run.setText("Pause");}});
        reset.addActionListener(e->{runTimer.stop();run.setText("Run");resetMachine();});
        inject.addActionListener(e->injectError()); enqueue.addActionListener(e->enqueueValue()); dequeue.addActionListener(e->dequeueValue());
        SwingUtilities.invokeLater(()->{
            workArea.setDividerLocation(0.34);
            memoryTrace.setDividerLocation(0.62);
            rightStack.setDividerLocation(0.78);
            source.setCaretPosition(0);
        });
    }

    private void loadProgram(){
        runTimer.stop(); pc=0; program.clear();labels.clear();w=0;sp=0;status=0;halted=false;
        String[] lines=source.getText().split("\\R");
        for(String line:lines){String s=line.split(";",2)[0].trim();if(s.isEmpty())continue;
            if(s.contains(":")){String[] a=s.split(":",2);labels.put(a[0].trim().toUpperCase(),program.size());s=a[1].trim();if(s.isEmpty())continue;}
            program.add(s);
        }
        source.setCaretPosition(0);
        trace.setText("Program loaded: "+program.size()+" instructions.\n");
        log("Loaded assembly program.");refresh();
    }
    private void resetMachine(){Arrays.fill(ram,0);fifo.clear();fifoModel.clear();trace.setText("");errors.setText("");loadProgram();log("Machine reset.");refresh();}
    private void step(){
        if(halted||pc<0||pc>=program.size()){halted=true;refresh();runTimer.stop();return;}
        int address=pc;String line=program.get(pc++);String[] parts=line.replace(","," ").trim().split("\\s+");
        String op=parts[0].toUpperCase();String arg=parts.length>1?parts[1]:"";int before=w;
        try{
            switch(op){
                case "MOVLW": w=value(arg)&255;break;
                case "MOVWF": ram[address(arg)]=w;break;
                case "MOVF": w=ram[address(arg)];zero(w);break;
                case "CLRF": ram[address(arg)]=0;zero(0);break;
                case "INCF": {int a=address(arg);ram[a]=(ram[a]+1)&255;zero(ram[a]);break;}
                case "DECF": {int a=address(arg);ram[a]=(ram[a]-1)&255;zero(ram[a]);break;}
                case "ADDLW": {int n=w+value(arg);setFlag(0,n>255);w=n&255;zero(w);break;}
                case "SUBLW": {int n=value(arg)-w;setFlag(0,value(arg)>=w);w=n&255;zero(w);break;}
                case "ANDLW": w&=value(arg);zero(w);break;
                case "IORLW": w|=value(arg);zero(w);break;
                case "XORLW": w^=value(arg);zero(w);break;
                case "ANDWF": {int a=address(arg);ram[a]&=w;zero(ram[a]);break;}
                case "IORWF": {int a=address(arg);ram[a]|=w;zero(ram[a]);break;}
                case "XORWF": {int a=address(arg);ram[a]^=w;zero(ram[a]);break;}
                case "GOTO": pc=target(arg);break;
                case "BTFSC": {String[] a=arg.split("\\s+");if(((ram[address(a[0])]>>value(a[1]))&1)==0)pc++;break;}
                case "BTFSS": {String[] a=arg.split("\\s+");if(((ram[address(a[0])]>>value(a[1]))&1)!=0)pc++;break;}
                case "BSF": {String[] a=arg.split("\\s+");int m=address(a[0]);ram[m]|=1<<value(a[1]);break;}
                case "BCF": {String[] a=arg.split("\\s+");int m=address(a[0]);ram[m]&=~(1<<value(a[1]));break;}
                case "HENC": {int data=address(arg), code=address(parts[2]);int word=Hamming128.encode(ram[data]);ram[code]=word&255;ram[(code+1)&255]=(word>>8)&15;
                    trace.append(String.format("      Hamming code = %12s (0x%03X)%n",Integer.toBinaryString(0x1000|word).substring(1),word));break;}
                case "HDEC": {int data=address(arg),code=address(parts[2]);int word=ram[code]|((ram[(code+1)&255]&15)<<8);int syn=Hamming128.syndrome(word);int corrected=Hamming128.decode(word);
                    if(syn!=0){log(String.format("Single-bit error detected: syndrome %d. Corrected codeword bit %d.",syn,syn-1));}
                    else log("No bit error detected.");
                    ram[data]=corrected;trace.append(String.format("      Recovered data = 0x%02X (%d)%n",corrected,corrected));break;}
                case "FLIP": {int code=address(arg),bit=value(parts[2]);int word=ram[code]|((ram[(code+1)&255]&15)<<8);word=Hamming128.flip(word,bit);ram[code]=word&255;ram[(code+1)&255]=(word>>8)&15;log("Injected one bit error at codeword bit "+bit+".");break;}
                case "SLEEP": halted=true;break;
                case "NOP": break;
                default: throw new IllegalArgumentException("Unknown instruction: "+op);
            }
            trace.append(String.format("PC %02d  %-8s %-14s W:%02X -> %02X%n",address,op,arg,before,w));
        }catch(Exception ex){log("Assembly error at line "+(address+1)+": "+ex.getMessage());halted=true;}
        refresh();if(halted){runTimer.stop();runButton.setText("Run");}
    }
    private void injectError(){int word=ram[0x30]|((ram[0x31]&15)<<8);word=Hamming128.flip(word,2);ram[0x30]=word&255;ram[0x31]=(word>>8)&15;log("Manual error injection: flipped codeword bit 2.");refresh();}
    private void enqueueValue(){String s=JOptionPane.showInputDialog(this,"Enter a decimal byte (0-255):","FIFO enqueue",JOptionPane.QUESTION_MESSAGE);if(s==null)return;try{int n=Integer.parseInt(s.trim());if(n<0||n>255)throw new NumberFormatException();fifo.add(n);refresh();}catch(Exception ex){log("FIFO accepts values from 0 to 255.");}}
    private void dequeueValue(){if(fifo.isEmpty())log("FIFO is empty.");else log("Dequeued "+fifo.remove());refresh();}
    private int address(String s){return value(s)&255;}
    private int target(String s){String k=s.toUpperCase();return labels.containsKey(k)?labels.get(k):value(s);}
    private int value(String s){String t=s.trim().replace("#","");if(t.startsWith("0x")||t.startsWith("0X"))return Integer.parseInt(t.substring(2),16);if(t.endsWith("H")||t.endsWith("h"))return Integer.parseInt(t.substring(0,t.length()-1),16);return Integer.parseInt(t);}
    private void zero(int n){setFlag(2,(n&255)==0);}
    private void setFlag(int bit,boolean on){if(on)status|=1<<bit;else status&=~(1<<bit);}
    private void log(String s){errors.append(s+"\n");errors.setCaretPosition(errors.getDocument().getLength());}
    private void refresh(){
        wLabel.setText(String.format("W: 0x%02X (%d)",w,w));pcLabel.setText("PC: "+pc);spLabel.setText("SP: "+sp);
        flagsLabel.setText("Zero: "+((status>>2)&1)+"  Carry: "+(status&1)+"  DC: "+((status>>1)&1));haltLabel.setText("Halted: "+(halted?"Yes":"No"));
        ramModel.setRowCount(0);for(int i=0;i<256;i++)ramModel.addRow(new Object[]{String.format("0x%02X",i),String.format("%02X",ram[i]),ram[i],String.format("%8s",Integer.toBinaryString(ram[i])).replace(' ','0')});
        fifoModel.clear();for(int n:fifo)fifoModel.addElement(Integer.toString(n));
    }
    public static void main(String[] args){SwingUtilities.invokeLater(()->new SimulatorFrame().setVisible(true));}
}
