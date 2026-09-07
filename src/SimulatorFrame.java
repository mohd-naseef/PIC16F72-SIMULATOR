import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;

/** Educational desktop-style visual front end for the PIC16F72 CPU model. */
public class SimulatorFrame extends JFrame {
    private static final Color BACKGROUND = new Color(245, 247, 250);
    private static final Color NAVY = new Color(31, 54, 77);
    private static final Color ACCENT = new Color(42, 113, 174);
    private static final Font MONO = new Font(Font.MONOSPACED, Font.PLAIN, 13);
    private ProgramMemory programMemory;
    private DataMemory dataMemory;
    private CPU cpu;
    private GPIO gpio;
    private Timer0 timer0;
    private final List<String> programLines = new ArrayList<>();
    private JList<String> programList;
    private JTextArea console;
    private JLabel wValue, pcValue, statusValue, memoryValue, timerValue, portValue, stateValue;
    private JButton stepButton, runButton;
    private Timer runTimer;

    public SimulatorFrame() {
        super("PIC16F72 Simulator");
        initialiseModel();
        buildInterface();
        refreshView();
    }

    private void initialiseModel() {
        programMemory = new ProgramMemory(); dataMemory = new DataMemory(); gpio = new GPIO(); timer0 = new Timer0(); timer0.counter = 0;
        add("MOVLW", 10); add("MOVWF", 20); add("SUBLW", 15); add("ANDLW", 3); add("MOVLW", 5);
        add("ADDLW", 3); add("INCF", 20); add("GOTO", 9); add("MOVLW", 99); add("SLEEP", 0);
        cpu = new CPU(programMemory, dataMemory);
    }

    private void add(String opcode, int operand) {
        programMemory.addInstruction(new Instruction(opcode, operand));
        String operandText = opcode.equals("SLEEP") ? "" : String.format("0x%02X", operand);
        programLines.add(String.format("%02d    %-7s %s", programLines.size(), opcode, operandText));
    }

    private void buildInterface() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); setMinimumSize(new Dimension(1020, 620)); setSize(1220, 720); setLocationByPlatform(true);
        getContentPane().setBackground(BACKGROUND); setLayout(new BorderLayout(8, 8));
        add(createHeader(), BorderLayout.NORTH); add(createProgramPanel(), BorderLayout.WEST); add(createConsolePanel(), BorderLayout.CENTER);
        add(createHardwarePanel(), BorderLayout.EAST); add(createControls(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout()); header.setBackground(NAVY); header.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        JLabel title = new JLabel("PIC16F72  •  EDUCATIONAL MICROCONTROLLER SIMULATOR"); title.setForeground(Color.WHITE); title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        stateValue = new JLabel(); stateValue.setForeground(new Color(195, 225, 255)); stateValue.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        header.add(title, BorderLayout.WEST); header.add(stateValue, BorderLayout.EAST); return header;
    }

    private JPanel createProgramPanel() {
        JPanel panel = titledPanel("PROGRAM MEMORY", 245); programList = new JList<>(programLines.toArray(new String[0]));
        programList.setFont(MONO); programList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); programList.setBackground(Color.WHITE); programList.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        panel.add(new JScrollPane(programList), BorderLayout.CENTER);
        JLabel note = new JLabel("Address     Opcode     Operand"); note.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11)); note.setBorder(BorderFactory.createEmptyBorder(5, 7, 5, 7)); panel.add(note, BorderLayout.SOUTH); return panel;
    }

    private JPanel createConsolePanel() {
        JPanel panel = titledPanel("EXECUTION CONSOLE", 0); console = new JTextArea(); console.setEditable(false); console.setFont(MONO); console.setForeground(new Color(27, 45, 62)); console.setBackground(Color.WHITE); console.setMargin(new java.awt.Insets(10, 12, 10, 12));
        console.setText("Ready. Press STEP to execute one instruction, or RUN to execute automatically.\n"); panel.add(new JScrollPane(console), BorderLayout.CENTER); return panel;
    }

    private JPanel createHardwarePanel() {
        JPanel outer = titledPanel("PIC16F72 HARDWARE STATE", 270); JPanel values = new JPanel(new GridLayout(0, 1, 4, 4)); values.setBackground(BACKGROUND); values.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        wValue = readout(values, "W REGISTER"); pcValue = readout(values, "PROGRAM COUNTER"); statusValue = readout(values, "STATUS  [ Z  DC  C ]"); memoryValue = readout(values, "RAM[0x14]"); timerValue = readout(values, "TIMER0"); portValue = readout(values, "PORT A"); outer.add(values, BorderLayout.NORTH);
        JPanel gpioPanel = new JPanel(new GridLayout(2, 1, 4, 4)); gpioPanel.setBackground(BACKGROUND); gpioPanel.setBorder(BorderFactory.createTitledBorder("GPIO input"));
        JButton togglePin = new JButton("Toggle RA0 button"); togglePin.addActionListener(e -> { gpio.pinTrigger = !gpio.pinTrigger; gpio.portA = gpio.pinTrigger ? 1 : 0; log("GPIO: RA0 is now " + (gpio.pinTrigger ? "HIGH" : "LOW")); refreshView(); });
        JButton clear = new JButton("Clear GPIO flag"); clear.addActionListener(e -> { gpio.clearPinTrigger(); gpio.portA = 0; refreshView(); }); gpioPanel.add(togglePin); gpioPanel.add(clear); outer.add(gpioPanel, BorderLayout.CENTER); return outer;
    }

    private JLabel readout(JPanel parent, String label) {
        JPanel row = new JPanel(new BorderLayout(6, 0)); row.setBackground(Color.WHITE); row.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(220, 226, 232)), BorderFactory.createEmptyBorder(7, 8, 7, 8)));
        JLabel name = new JLabel(label); name.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10)); name.setForeground(new Color(92, 104, 116));
        JLabel value = new JLabel(); value.setHorizontalAlignment(SwingConstants.RIGHT); value.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13)); value.setForeground(ACCENT); row.add(name, BorderLayout.WEST); row.add(value, BorderLayout.EAST); parent.add(row); return value;
    }

    private JPanel createControls() {
        JPanel controls = new JPanel(); controls.setBackground(BACKGROUND); controls.setBorder(BorderFactory.createEmptyBorder(4, 8, 10, 8)); stepButton = new JButton("STEP"); runButton = new JButton("RUN"); JButton resetButton = new JButton("RESET"); JButton clearConsole = new JButton("CLEAR CONSOLE");
        stepButton.addActionListener(e -> executeStep()); runButton.addActionListener(e -> toggleRun()); resetButton.addActionListener(e -> resetSimulator()); clearConsole.addActionListener(e -> console.setText("")); controls.add(stepButton); controls.add(runButton); controls.add(resetButton); controls.add(clearConsole); runTimer = new Timer(520, e -> executeStep()); return controls;
    }

    private JPanel titledPanel(String title, int width) {
        JPanel panel = new JPanel(new BorderLayout(6, 6)); panel.setBackground(BACKGROUND); TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(196, 205, 214)), title); border.setTitleFont(new Font(Font.SANS_SERIF, Font.BOLD, 11)); border.setTitleColor(NAVY); panel.setBorder(border); if (width > 0) panel.setPreferredSize(new Dimension(width, 0)); return panel;
    }

    private void executeStep() {
        if (cpu.isHalted()) { stopRun(); return; }
        int address = cpu.getPC(); Instruction instruction = cpu.fetch();
        if (instruction == null) { log("Program counter left program memory. Simulation stopped."); stopRun(); refreshView(); return; }
        cpu.execute(); timer0.tick(); log(String.format("PC %02d  %-7s 0x%02X      W=0x%02X  STATUS=%s", address, instruction.getOpcode(), instruction.getOperand(), cpu.getW(), flags())); refreshView();
        if (cpu.isHalted()) { log("SLEEP executed. Program finished."); stopRun(); }
    }

    private void toggleRun() { if (runTimer.isRunning()) stopRun(); else if (!cpu.isHalted()) { runTimer.start(); runButton.setText("PAUSE"); stepButton.setEnabled(false); } }
    private void stopRun() { runTimer.stop(); runButton.setText("RUN"); stepButton.setEnabled(!cpu.isHalted()); }
    private void resetSimulator() { stopRun(); programLines.clear(); initialiseModel(); programList.setListData(programLines.toArray(new String[0])); console.setText("Simulator reset. Program loaded and ready.\n"); refreshView(); }
    private String flags() { int status = cpu.getSTATUS(); return String.format("%d  %d   %d", (status >> 2) & 1, (status >> 1) & 1, status & 1); }
    private void refreshView() {
        wValue.setText(String.format("0x%02X  (%3d)", cpu.getW(), cpu.getW())); pcValue.setText(String.format("0x%02X  (%3d)", cpu.getPC(), cpu.getPC())); statusValue.setText(flags()); memoryValue.setText(String.format("0x%02X", dataMemory.read(20))); timerValue.setText(String.format("0x%02X", timer0.counter)); portValue.setText(gpio.pinTrigger ? "RA0 = HIGH" : "RA0 = LOW"); stateValue.setText(cpu.isHalted() ? "● HALTED" : "● READY"); int pc = cpu.getPC(); programList.setSelectedIndex(pc >= 0 && pc < programLines.size() ? pc : -1); stepButton.setEnabled(!cpu.isHalted() && !runTimer.isRunning());
    }
    private void log(String message) { console.append(message + "\n"); console.setCaretPosition(console.getDocument().getLength()); }
}
