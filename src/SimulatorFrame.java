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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;

/**
 * Educational desktop-style visual front end for the
 * PIC16F72 CPU model with OS Scheduling and Week 3 FIFO Queue.
 */
public class SimulatorFrame extends JFrame {

    private static final Color BACKGROUND =
            new Color(245, 247, 250);

    private static final Color NAVY =
            new Color(31, 54, 77);

    private static final Color ACCENT =
            new Color(42, 113, 174);

    private static final Font MONO =
            new Font(Font.MONOSPACED, Font.PLAIN, 13);

    // Week 2 components
    private ProgramMemory programMemory;
    private DataMemory dataMemory;
    private CPU cpu;
    private GPIO gpio;
    private Timer0 timer0;
    private Scheduler scheduler;

    // Week 3 FIFO Queue
    private FIFOQueue fifoQueue;

    private final List<String> programLines =
            new ArrayList<>();

    private JList<String> programList;

    private JTextArea console;

    private JLabel wValue;
    private JLabel pcValue;
    private JLabel statusValue;
    private JLabel memoryValue;
    private JLabel timerValue;
    private JLabel portValue;
    private JLabel stateValue;

    // Week 3 queue display
    private JLabel queueValue;

    private JLabel currentTaskValue;

    private JButton stepButton;
    private JButton runButton;
    private JButton loadButton;

    private Timer runTimer;

    private boolean isLoaded = false;

    // Used to generate simple queue test values
    private int nextQueueValue = 10;
    // Week 3 Stack UI components
    private JLabel spValue;
    private JTextArea stackDisplayArea;
    private JTextField stackInputField;
    private JButton pushButton;
    private JButton popButton;
    private JButton peekButton;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public SimulatorFrame() {

        super("PIC16F72 Simulator & OS Scheduler");

        initialiseModel();

        buildInterface();

        refreshView();
    }


    // =========================================================
    // INITIALISE MODEL
    // =========================================================

    private void initialiseModel() {

        // Week 2
        programMemory = new ProgramMemory();

        dataMemory = new DataMemory();

        gpio = new GPIO();

        timer0 = new Timer0();
        timer0.counter = 0;

        cpu = new CPU(
                programMemory,
                dataMemory
        );

        scheduler = new Scheduler(2);

        // Week 3 FIFO Queue
        fifoQueue = new FIFOQueue(8);

        // Reset queue test values
        nextQueueValue = 10;
    }


    // =========================================================
    // LOAD PROGRAM
    // =========================================================

    private void loadTasks() {

        stopRun();

        programLines.clear();

        initialiseModel();

        // -----------------------------------------
        // Task 1: addresses 0-4
        // -----------------------------------------

        add("MOVLW", 10);

        add("MOVWF", 20);

        add("ADDLW", 5);

        add("SUBLW", 2);

        add("SLEEP", 0);


        // -----------------------------------------
        // Task 2: addresses 5-9
        // -----------------------------------------

        add("MOVLW", 50);

        add("ADDLW", 10);

        add("ANDLW", 15);

        add("INCF", 20);

        add("SLEEP", 0);


        // -----------------------------------------
        // Initialise OS Scheduler
        // -----------------------------------------

        scheduler.addTask(
                new Task(1, "Task 1", 0)
        );

        scheduler.addTask(
                new Task(2, "Task 2", 5)
        );


        isLoaded = true;

        programList.setListData(
                programLines.toArray(new String[0])
        );

        console.setText(
                "Program loaded. OS Scheduler ready with Task 1 and Task 2.\n"
                + "Press STEP or RUN.\n\n"
        );

        refreshView();
    }


    // =========================================================
    // ADD INSTRUCTION
    // =========================================================

    private void add(
            String opcode,
            int operand
    ) {

        programMemory.addInstruction(
                new Instruction(opcode, operand)
        );

        String operandText =
                opcode.equals("SLEEP")
                        ? ""
                        : String.format(
                                "0x%02X",
                                operand
                        );

        programLines.add(
                String.format(
                        "%02d    %-7s %s",
                        programLines.size(),
                        opcode,
                        operandText
                )
        );
    }


    // =========================================================
    // BUILD INTERFACE
    // =========================================================

    private void buildInterface() {

        setDefaultCloseOperation(
                JFrame.EXIT_ON_CLOSE
        );

        setMinimumSize(
                new Dimension(1020, 640)
        );

        setSize(1220, 720);

        setLocationByPlatform(true);

        getContentPane().setBackground(
                BACKGROUND
        );

        setLayout(
                new BorderLayout(8, 8)
        );


        add(
                createHeader(),
                BorderLayout.NORTH
        );

        add(
                createProgramPanel(),
                BorderLayout.WEST
        );

        add(
                createConsolePanel(),
                BorderLayout.CENTER
        );

        add(
                createHardwarePanel(),
                BorderLayout.EAST
        );

        add(
                createControls(),
                BorderLayout.SOUTH
        );
    }


    // =========================================================
    // HEADER
    // =========================================================

    private JPanel createHeader() {

        JPanel header =
                new JPanel(
                        new BorderLayout()
                );

        header.setBackground(NAVY);

        header.setBorder(
                BorderFactory.createEmptyBorder(
                        12,
                        16,
                        12,
                        16
                )
        );

        JLabel title =
                new JLabel(
                        "PIC16F72  •  EDUCATIONAL SIMULATOR WITH OS SCHEDULING"
                );

        title.setForeground(Color.WHITE);

        title.setFont(
                new Font(
                        Font.SANS_SERIF,
                        Font.BOLD,
                        16
                )
        );

        stateValue = new JLabel();

        stateValue.setForeground(
                new Color(195, 225, 255)
        );

        stateValue.setFont(
                new Font(
                        Font.SANS_SERIF,
                        Font.BOLD,
                        12
                )
        );

        header.add(
                title,
                BorderLayout.WEST
        );

        header.add(
                stateValue,
                BorderLayout.EAST
        );

        return header;
    }


    // =========================================================
    // PROGRAM PANEL
    // =========================================================

    private JPanel createProgramPanel() {

        JPanel panel =
                titledPanel(
                        "PROGRAM MEMORY",
                        245
                );

        programList =
                new JList<>(
                        programLines.toArray(
                                new String[0]
                        )
                );

        programList.setFont(MONO);

        programList.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        programList.setBackground(
                Color.WHITE
        );

        programList.setBorder(
                BorderFactory.createEmptyBorder(
                        8,
                        10,
                        8,
                        10
                )
        );

        panel.add(
                new JScrollPane(programList),
                BorderLayout.CENTER
        );

        JLabel note =
                new JLabel(
                        "Address     Opcode     Operand"
                );

        note.setFont(
                new Font(
                        Font.MONOSPACED,
                        Font.PLAIN,
                        11
                )
        );

        note.setBorder(
                BorderFactory.createEmptyBorder(
                        5,
                        7,
                        5,
                        7
                )
        );

        panel.add(
                note,
                BorderLayout.SOUTH
        );

        return panel;
    }


    // =========================================================
    // EXECUTION CONSOLE
    // =========================================================

    private JPanel createConsolePanel() {

        JPanel panel =
                titledPanel(
                        "EXECUTION CONSOLE",
                        0
                );

        console = new JTextArea();

        console.setEditable(false);

        console.setFont(MONO);

        console.setForeground(
                new Color(27, 45, 62)
        );

        console.setBackground(
                Color.WHITE
        );

        console.setMargin(
                new java.awt.Insets(
                        10,
                        12,
                        10,
                        12
                )
        );

        console.setText(
                "Ready. Click LOAD to load program.\n\n"
        );

        panel.add(
                new JScrollPane(console),
                BorderLayout.CENTER
        );

        return panel;
    }


    // =========================================================
    // HARDWARE PANEL
    // =========================================================

    private JPanel createHardwarePanel() {

        JPanel outer =
                titledPanel(
                        "HARDWARE & OS STATE",
                        340
                );

        JPanel values =
                new JPanel(
                        new GridLayout(
                                0,
                                1,
                                4,
                                4
                        )
                );

        values.setBackground(
                BACKGROUND
        );

        values.setBorder(
                BorderFactory.createEmptyBorder(
                        8,
                        8,
                        8,
                        8
                )
        );


        // Week 2 displays

        currentTaskValue =
                readout(
                        values,
                        "ACTIVE TASK"
                );

        wValue =
                readout(
                        values,
                        "W REGISTER"
                );

        pcValue =
                readout(
                        values,
                        "PROGRAM COUNTER"
                );

        statusValue =
                readout(
                        values,
                        "STATUS  [ Z  DC  C ]"
                );

        memoryValue =
                readout(
                        values,
                        "RAM[0x14]"
                );


        // Week 3 FIFO display

        queueValue =
                readout(
                        values,
                        "FIFO QUEUE"
                );


        timerValue =
                readout(
                        values,
                        "TIMER0"
                );

        portValue =
                readout(
                        values,
                        "PORT A"
                );


        outer.add(
                values,
                BorderLayout.NORTH
        );


        // =====================================================
        // GPIO
        // =====================================================

        JPanel gpioPanel =
                new JPanel(
                        new GridLayout(
                                2,
                                1,
                                4,
                                4
                        )
                );

        gpioPanel.setBackground(
                BACKGROUND
        );

        gpioPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "GPIO input"
                )
        );


        JButton togglePin =
                new JButton(
                        "Toggle RA0 button"
                );

        togglePin.addActionListener(e -> {

            gpio.pinTrigger =
                    !gpio.pinTrigger;

            gpio.portA =
                    gpio.pinTrigger
                            ? 1
                            : 0;

            log(
                    "GPIO: RA0 is now "
                    + (
                            gpio.pinTrigger
                                    ? "HIGH"
                                    : "LOW"
                    )
            );

            refreshView();
        });


        JButton clear =
                new JButton(
                        "Clear GPIO flag"
                );

        clear.addActionListener(e -> {

            gpio.clearPinTrigger();

            gpio.portA = 0;

            refreshView();
        });


        gpioPanel.add(togglePin);

        gpioPanel.add(clear);

       JPanel lowerPanel = new JPanel();
lowerPanel.setLayout(new javax.swing.BoxLayout(lowerPanel, javax.swing.BoxLayout.Y_AXIS));
lowerPanel.setBackground(BACKGROUND);

lowerPanel.add(gpioPanel);
lowerPanel.add(javax.swing.Box.createVerticalStrut(8));
lowerPanel.add(createStackPanel());

outer.add(lowerPanel, BorderLayout.CENTER);
        return outer;
    }


    // =========================================================
    // READOUT
    // =========================================================

    private JLabel readout(
            JPanel parent,
            String label
    ) {

        JPanel row =
                new JPanel(
                        new BorderLayout(6, 0)
                );

        row.setBackground(
                Color.WHITE
        );

        row.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        220,
                                        226,
                                        232
                                )
                        ),
                        BorderFactory.createEmptyBorder(
                                7,
                                8,
                                7,
                                8
                        )
                )
        );


        JLabel name =
                new JLabel(label);

        name.setFont(
                new Font(
                        Font.SANS_SERIF,
                        Font.BOLD,
                        10
                )
        );

        name.setForeground(
                new Color(
                        92,
                        104,
                        116
                )
        );


        JLabel value =
                new JLabel();

        value.setHorizontalAlignment(
                SwingConstants.RIGHT
        );

        value.setFont(
                new Font(
                        Font.MONOSPACED,
                        Font.BOLD,
                        13
                )
        );

        value.setForeground(
                ACCENT
        );


        row.add(
                name,
                BorderLayout.WEST
        );

        row.add(
                value,
                BorderLayout.EAST
        );

        parent.add(row);

        return value;
    }


    // =========================================================
    // CONTROLS
    // =========================================================

    private JPanel createControls() {

        JPanel controls =
                new JPanel();

        controls.setBackground(
                BACKGROUND
        );

        controls.setBorder(
                BorderFactory.createEmptyBorder(
                        4,
                        8,
                        10,
                        8
                )
        );


        // Week 2 buttons

        loadButton =
                new JButton("LOAD");

        stepButton =
                new JButton("STEP");

        runButton =
                new JButton("RUN");

        JButton resetButton =
                new JButton("RESET");


        // Week 3 buttons

        JButton enqueueButton =
                new JButton("ENQUEUE");

        JButton dequeueButton =
                new JButton("DEQUEUE");


        JButton clearConsole =
                new JButton(
                        "CLEAR CONSOLE"
                );


        // Week 2 actions

        loadButton.addActionListener(
                e -> loadTasks()
        );

        stepButton.addActionListener(
                e -> executeStep()
        );

        runButton.addActionListener(
                e -> toggleRun()
        );

        resetButton.addActionListener(
                e -> resetSimulator()
        );


        // Week 3 actions

        enqueueButton.addActionListener(
                e -> enqueueTestValue()
        );

        dequeueButton.addActionListener(
                e -> dequeueValue()
        );


        clearConsole.addActionListener(
                e -> console.setText("")
        );


        // Add buttons

        controls.add(loadButton);

        controls.add(stepButton);

        controls.add(runButton);

        controls.add(resetButton);

        controls.add(enqueueButton);

        controls.add(dequeueButton);

        controls.add(clearConsole);
        JButton gpioButton = new JButton("Toggle GPIO Pin");
        gpioButton.setFont(MONO);
        gpioButton.addActionListener(e -> {
            if (!gpio.pinTrigger) {
                gpio.pressButton();
                console.append("[GPIO] Button pressed: Pin trigger set HIGH\n");
            } else {
                gpio.clearPinTrigger();
                console.append("[GPIO] Pin trigger cleared: set LOW\n");
            }
            refreshView();
        });
        controls.add(gpioButton);


        // Run timer

        runTimer =
                new Timer(
                        520,
                        e -> executeStep()
                );


        return controls;
    }


    // =========================================================
    // TITLED PANEL
    // =========================================================

    private JPanel titledPanel(
            String title,
            int width
    ) {

        JPanel panel =
                new JPanel(
                        new BorderLayout(
                                6,
                                6
                        )
                );

        panel.setBackground(
                BACKGROUND
        );


        TitledBorder border =
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(
                                new Color(
                                        196,
                                        205,
                                        214
                                )
                        ),
                        title
                );


        border.setTitleFont(
                new Font(
                        Font.SANS_SERIF,
                        Font.BOLD,
                        11
                )
        );

        border.setTitleColor(NAVY);

        panel.setBorder(border);


        if (width > 0) {

            panel.setPreferredSize(
                    new Dimension(
                            width,
                            0
                    )
            );
        }


        return panel;
    }


    // =========================================================
    // WEEK 2 CPU STEP
    // =========================================================

    private void executeStep() {

        Task prevTask =
                scheduler.getCurrentTask();

        int prevPC =
                cpu.getPC();


        // Execute step via OS Scheduler

        scheduler.step(cpu);

        timer0.tick();


        Task currentTask =
                scheduler.getCurrentTask();


        // Check context switch

        if (
                prevTask != null
                        &&
                currentTask != null
                        &&
                !prevTask.equals(currentTask)
        ) {

            log(
                    String.format(
                            ">>> [CONTEXT SWITCH] %s -> %s "
                                    + "(Restored PC: 0x%02X)",
                            prevTask.getTaskName(),
                            currentTask.getTaskName(),
                            cpu.getPC()
                    )
            );

        } else if (
                currentTask != null
        ) {

            log(
                    String.format(
                            "[%s] PC: 0x%02X | W: 0x%02X | STATUS: %s",
                            currentTask.getTaskName(),
                            prevPC,
                            cpu.getW(),
                            flags()
                    )
            );
        }


        refreshView();


        if (
                currentTask == null
                        &&
                cpu.isHalted()
        ) {

            log(
                    "\nAll tasks completed execution."
            );

            stopRun();
        }
    }


    // =========================================================
    // RUN / PAUSE
    // =========================================================

    private void toggleRun() {

        if (runTimer.isRunning()) {

            stopRun();

        } else {

            runTimer.start();

            runButton.setText(
                    "PAUSE"
            );

            stepButton.setEnabled(false);
        }
    }


    // =========================================================
    // STOP
    // =========================================================

    private void stopRun() {

        runTimer.stop();

        runButton.setText(
                "RUN"
        );

        stepButton.setEnabled(true);
    }


    // =========================================================
    // RESET
    // =========================================================

    private void resetSimulator() {

        stopRun();

        isLoaded = false;

        programLines.clear();

        initialiseModel();

        // Reset Week 3 queue value generation
        nextQueueValue = 10;

        programList.setListData(
                new String[0]
        );

        console.setText(
                "Simulator reset. "
                        + "Click LOAD to load program.\n"
        );

        refreshView();
    }


    // =========================================================
    // WEEK 3 - ENQUEUE
    // =========================================================


private void enqueueTestValue() {

    int value = nextQueueValue;

    boolean success = fifoQueue.enqueue(value);

    if (success) {

        log(
                String.format(
                        "QUEUE: ENQUEUE %d (0x%02X)",
                        value,
                        value
                )
        );

        nextQueueValue += 10;

    } else {

        log(
                "QUEUE ERROR: Queue is full. "
                        + value
                        + " was not added."
        );
    }

    refreshView();
}


    // =========================================================
    // WEEK 3 - DEQUEUE
    // =========================================================

    private void dequeueValue() {

        try {

            int value =
                    fifoQueue.dequeue();


            log(
                    String.format(
                            "QUEUE: DEQUEUE %d (0x%02X)",
                            value,
                            value
                    )
            );


            refreshView();


        } catch (
                IllegalStateException e
        ) {

            log(
                    "QUEUE ERROR: "
                            + e.getMessage()
            );
        }
    }


    // =========================================================
    // STATUS FLAGS
    // =========================================================

    private String flags() {

        int status =
                cpu.getSTATUS();


        return String.format(
                "%d  %d   %d",
                (status >> 2) & 1,
                (status >> 1) & 1,
                status & 1
        );
    }


    // =========================================================
    // REFRESH GUI
    // =========================================================

     {
   Task t = (scheduler != null) ? scheduler.getCurrentTask() : null;

        // Active task

      if (currentTaskValue != null) {
    currentTaskValue.setText(
            t != null
                    ? t.getTaskName()
                    : "None / Idle"
    );
}
}

        // CPU
private void refreshView() {
        wValue.setText(
                String.format(
                        "0x%02X  (%3d)",
                        cpu.getW(),
                        cpu.getW()
                )
        );

        pcValue.setText(
                String.format(
                        "0x%02X  (%3d)",
                        cpu.getPC(),
                        cpu.getPC()
                )
        );

        statusValue.setText(
                flags()
        );

        // Memory
        memoryValue.setText(
                String.format(
                        "0x%02X",
                        dataMemory.read(20)
                )
        );

        // =====================================================
        // WEEK 3 FIFO QUEUE DISPLAY
        // =====================================================
        queueValue.setText(
                String.format(
                        "size=%d %s",
                        fifoQueue.size(),
                        fifoQueue.toString()
                )
        );

        // Timer
        timerValue.setText(
                String.format(
                        "0x%02X (%3d) | OVF: %s",
                        timer0.counter,
                        timer0.counter,
                        timer0.overflow ? "YES" : "NO"
                )
        );

        // GPIO
        portValue.setText(
                gpio.pinTrigger
                        ? "RA0 = HIGH"
                        : "RA0 = LOW"
        );

        // Overall state
        Task t = (scheduler != null) ? scheduler.getCurrentTask() : null;
        stateValue.setText(
                t != null
                        ? "● RUNNING (" + t.getTaskName() + ")"
                        : "○ IDLE"
        );

        // Program selection
        int pc = cpu.getPC();
        if (pc >= 0 && pc < programLines.size()) {
            programList.setSelectedIndex(pc);
            programList.ensureIndexIsVisible(pc);
        }

        stepButton.setEnabled(
                isLoaded
                        &&
                !runTimer.isRunning()
        );

        runButton.setEnabled(
                isLoaded
        );

        // =========================================================
        // HARDWARE STACK UPDATE (WEEK 3)
        // =========================================================
        if (stackDisplayArea != null && spValue != null && dataMemory != null) {
            int currentSp = dataMemory.getSP();
            spValue.setText(String.format("SP: %d / 8", currentSp));

            StringBuilder sb = new StringBuilder();
            for (int i = 7; i >= 0; i--) {
                int val = dataMemory.getStackElement(i);
                String ptr = (i == currentSp - 1 && currentSp > 0) ? " <-- TOP" : "";
                sb.append(String.format("Level [%d]: 0x%04X%s\n", i, val, ptr));
            }
            stackDisplayArea.setText(sb.toString());
            stackDisplayArea.setCaretPosition(0);
        }
    }


    // =========================================================
    // CONSOLE LOG
    // =========================================================

    private void log(
            String message
    ) {

        console.append(
                message + "\n"
        );

        console.setCaretPosition(
                console.getDocument().getLength()
        );
    }
// =========================================================
    // STACK UI PANEL (WEEK 3)
    // =========================================================

    private JPanel createStackPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Hardware Stack (8-Level LIFO)"));
        panel.setBackground(BACKGROUND);

        spValue = new JLabel("SP: 0 / 8");
        spValue.setFont(MONO);
        panel.add(spValue, BorderLayout.NORTH);

        stackDisplayArea = new JTextArea(8, 20);
        stackDisplayArea.setEditable(false);
        stackDisplayArea.setFont(MONO);
        panel.add(new JScrollPane(stackDisplayArea), BorderLayout.CENTER);

       JPanel controls = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 2));
        controls.setBackground(BACKGROUND);

        stackInputField = new JTextField("0x0010", 6);
        pushButton = new JButton("PUSH");
        popButton = new JButton("POP");
        peekButton = new JButton("PEEK");

        controls.add(new JLabel("Val:"));
        controls.add(stackInputField);
        controls.add(pushButton);
        controls.add(popButton);
        controls.add(peekButton);

        panel.add(controls, BorderLayout.SOUTH);

        // Action Listeners
        pushButton.addActionListener(e -> {
            try {
                String text = stackInputField.getText().trim();
                int val = text.startsWith("0x") || text.startsWith("0X")
                        ? Integer.parseInt(text.substring(2), 16)
                        : Integer.parseInt(text);
                dataMemory.push(val);
                if (console != null) {
                    console.append(String.format("STACK: PUSH 0x%04X (SP=%d)\n", val & 0x1FFF, dataMemory.getSP()));
                }
                refreshView();
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Stack Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid integer or hex (e.g. 0x10)", "Input Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        popButton.addActionListener(e -> {
            try {
                int val = dataMemory.pop();
                if (console != null) {
                    console.append(String.format("STACK: POP -> 0x%04X (SP=%d)\n", val, dataMemory.getSP()));
                }
                refreshView();
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Stack Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        peekButton.addActionListener(e -> {
            int sp = dataMemory.getSP();
            if (sp <= 0) {
                JOptionPane.showMessageDialog(this, "Stack is empty!", "Stack Peek", JOptionPane.INFORMATION_MESSAGE);
            } else {
                int topVal = dataMemory.getStackElement(sp - 1);
                JOptionPane.showMessageDialog(this, String.format("Top of Stack (Level %d): 0x%04X", (sp - 1), topVal), "Stack Peek", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        return panel;
    }

    // =========================================================
    // MAIN
    // =========================================================

    public static void main(
            String[] args
    ) {

        javax.swing.SwingUtilities.invokeLater(
                () -> {

                    new SimulatorFrame()
                            .setVisible(true);
                }
        );
    }
}

