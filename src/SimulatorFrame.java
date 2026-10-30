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


    // Non-null only when launched as the separate UI Process.
    private CoreIPCClient uiIpc;

    private boolean isRemoteUI() {
        return uiIpc != null;
    }

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
        this(null);
    }

    /**
     * Creates the existing GUI. When ipc is non-null, all simulator
     * actions are sent to the Core Process rather than executed locally.
     */
    public SimulatorFrame(CoreIPCClient ipc) {
        super("PIC16F72 Simulator & OS Scheduler");

        this.uiIpc = ipc;

        // The model is initialized for compatibility with the existing GUI
        // components, but remote mode never executes operations against it.
        initialiseModel();
        buildInterface();
        refreshView();

        if (isRemoteUI()) {
            log("UI Process started. Simulator commands use Core IPC.");
        }
    }

   
/**
 * Sends a command to Core and displays the actual response.
 */
private void sendCoreCommand(String command) {
    if (!isRemoteUI()) {
        log("IPC connection is not available.");
        return;
    }

   

try {
    String response = uiIpc.sendCommand(command);
    // Handle FIFO queue response directly in the GUI.
if (command.trim().equalsIgnoreCase("QUEUE")) {
    if (response != null && response.startsWith("RESULT: QUEUE ")) {
        String contents = response.substring("RESULT: QUEUE ".length()).trim();

        if (queueValue != null) {
            queueValue.setText(contents);
        }

        log("FIFO Queue: " + contents);
    } else {
        log("FIFO Queue response: " + response);
    }
    return;
}
    if (command.trim().equalsIgnoreCase("QUEUE")) {
    showRemoteResponse(response);
    return;
}

if (command.trim().equalsIgnoreCase("DEQUEUE")
        || command.trim().toUpperCase().startsWith("ENQUEUE ")) {
    log("Queue operation: " + response);
    sendCoreCommand("QUEUE");
    return;
}
// Display PEEK result in the GUI instead of only the terminal.
if (command.trim().equalsIgnoreCase("PEEK")
        && response != null
        && response.startsWith("RESULT: STACK_TOP ")) {

    String value = response.substring("RESULT: STACK_TOP ".length()).trim();

    log("PEEK: Top of stack = " + value);

    JOptionPane.showMessageDialog(
        this,
        "Top of Stack: " + value,
        "Stack Peek",
        JOptionPane.INFORMATION_MESSAGE
    );

    return;
}
   
    // ENQUEUE / DEQUEUE / PUSH / POP: Display results in the GUI.
    if (response != null) {
        String cmd = command.trim().toUpperCase();

        if (cmd.startsWith("ENQUEUE ")
                || cmd.equals("DEQUEUE")
                || cmd.startsWith("PUSH ")
                || cmd.equals("POP")) {
            log("Operation result: " + response);
        }
    }


    // LOAD: Load the program and display all instructions.
    if (command.equalsIgnoreCase("LOAD")
            && response != null
            && response.startsWith("RESULT: PROGRAM_LOADED")) {

        isLoaded = true;
        showRemoteResponse(response);
        log("Program loaded successfully.");

        sendCoreCommand("LIST");

        stepButton.setEnabled(true);
        runButton.setEnabled(true);
        return;
    }

    // LIST: Display the complete program memory.
    if (response != null
            && response.startsWith("RESULT: PROGRAM_LIST ")) {

        showRemoteResponse(response);
        log("Program Memory: " + programLines.size()
                + " instructions loaded.");
        return;
    }

        // RESET: Reset CPU and memory.
        if (command.equalsIgnoreCase("RESET")
                && response.startsWith("RESULT: RESET_COMPLETE")) {
            showRemoteResponse(response);
            log("Simulator reset. PC=0, W=0, RAM[20]=0.");
            return;
        }

      // STEP: Display the instruction actually executed.
if (response.startsWith("RESULT: STEP_COMPLETE")) {
    showRemoteResponse(response);

    String executedPC = extractField(response, "executedPC");
    String executedTask = extractField(response, "executedTask");
    String instruction = extractField(response, "instruction");
    String executedW = extractField(response, "executedW");
    String nextPC = extractField(response, "nextPC");

    log("========== STEP EXECUTION ==========");
    log("Task        : " + executedTask);
    log("Executed PC : " + executedPC);
    log("Instruction : " + instruction);
    log("W Register  : " + executedW);
    log("Next PC     : " + nextPC);
    log("====================================");
    return;
}

        // Display all other Core responses.
        showRemoteResponse(response);
        } catch (Exception e) {
            log("IPC command failed: " + e.getMessage());
        }
    }
/**
 * Extracts a field from a Core response.
 */


private String extractField(String response, String field) {
    if (response == null || field == null) {
        return null;
    }

    String marker = field + "=";
    int start = response.indexOf(marker);

    if (start == -1) {
        return null;
    }

    start += marker.length();

    // These fields may contain spaces.
    String[] multiWordFields = {
        "task",
        "executedTask",
        "instruction"
    };

    boolean isMultiWord = false;

    for (String name : multiWordFields) {
        if (name.equals(field)) {
            isMultiWord = true;
            break;
        }
    }

    if (isMultiWord) {
        String[] nextFields = {
            "executedPC=",
            "instruction=",
            "executedW=",
            "nextPC=",
            "steps=",
            "allTasksFinished=",
            "trace="
        };

        int end = response.length();

        for (String nextField : nextFields) {
            int position = response.indexOf(nextField, start);

            if (position >= 0 && position < end) {
                end = position;
            }
        }

        return response.substring(start, end).trim();
    }

    int end = response.indexOf(' ', start);

    if (end == -1) {
        end = response.length();
    }

    return response.substring(start, end).trim();
}


/**
 * Refreshes the remote Core status.
 */
private void refreshRemoteStatus() {
    sendCoreCommand("STATUS");
}

    /**
     * Avoid showing local simulator values as if they came from Core.
     */
    
private void showRemoteResponse(String response) {
    if (response == null) {
        return;
    }

    // Display FIFO queue contents received through Core IPC.
    if (queueValue != null && response.startsWith("RESULT: QUEUE ")) {
        String queueStatus =
                response.substring("RESULT: QUEUE ".length()).trim();
        queueValue.setText(queueStatus);
    }

    // Display PEEK result in the GUI.
    if (response.startsWith("RESULT: STACK_TOP ")) {
        String value =
                response.substring("RESULT: STACK_TOP ".length()).trim();

        log("PEEK: Top of stack = " + value);

        JOptionPane.showMessageDialog(
                this,
                "Top of Stack: " + value,
                "Stack Peek",
                JOptionPane.INFORMATION_MESSAGE
        );
        return;
    }

    // PROGRAM LIST: Populate Program Memory.
    if (response.startsWith("RESULT: PROGRAM_LIST ")) {
        String listing = response.substring(
                "RESULT: PROGRAM_LIST ".length()
        );

        programLines.clear();

        for (String entry : listing.split(";")) {
            String[] fields = entry.split("\\|", -1);

            if (fields.length == 3) {
                try {
                    int address = Integer.parseInt(fields[0]);
                    String opcode = fields[1];
                    String operand = fields[2];

                    programLines.add(String.format(
                            "%02d       %-8s %s",
                            address, opcode, operand
                    ));
                } catch (NumberFormatException ex) {
                    log("Invalid program memory entry: " + entry);
                }
            }
        }

        programList.setListData(
                programLines.toArray(new String[0])
        );

        if (!programLines.isEmpty()) {
            programList.setSelectedIndex(0);
            programList.ensureIndexIsVisible(0);
        }

        isLoaded = !programLines.isEmpty();
        stepButton.setEnabled(isLoaded);
        runButton.setEnabled(isLoaded);
        return;
    }

    if (!response.startsWith("RESULT: ")) {
        return;
    }

    // Update fields that do not contain spaces.
    for (String field : response.split("\\s+")) {
        try {
            if (field.startsWith("pc=")) {
                int pc = Integer.parseInt(field.substring(3));
                pcValue.setText(String.valueOf(pc));

            } else if (field.startsWith("w=")) {
                int w = Integer.parseInt(field.substring(2));
                wValue.setText(String.format(
                        "0x%02X (%d)", w, w
                ));

            } else if (field.startsWith("ram20=")) {
                int ram = Integer.parseInt(field.substring(6));
                memoryValue.setText(String.format(
                        "0x%02X (%d)", ram, ram
                ));

            } else if (field.startsWith("sp=")) {
                spValue.setText(field.substring(3));

            } else if (field.startsWith("status=")) {
                int status = Integer.parseInt(field.substring(7));
                statusValue.setText(String.format(
                        "0x%02X", status
                ));
            }
        } catch (NumberFormatException ex) {
            log("Could not parse Core status field: " + field);
        }
    }

    // Task names may contain spaces, so do not split them at whitespace.
    String task = extractField(response, "task");
    if (task != null) {
        currentTaskValue.setText(task);
    }

  // During STEP, highlight the instruction that actually executed.
// For other responses, highlight the instruction at the current PC.
String instructionPC = extractField(response, "executedPC");

if (instructionPC == null) {
    instructionPC = extractField(response, "pc");
}

if (instructionPC != null) {
    try {
        int pc = Integer.parseInt(instructionPC);

        if (pc >= 0 && pc < programLines.size()) {
            programList.setSelectedIndex(pc);
            programList.ensureIndexIsVisible(pc);
        }
    } catch (NumberFormatException ignored) {
        // Ignore an invalid PC value.
    }
}


    if (timerValue != null) {
        timerValue.setText("Not provided by Core");
    }

    if (portValue != null) {
        portValue.setText("Not provided by Core");
    }

    if (stateValue != null) {
        stateValue.setText("UI PROCESS - IPC");
    }
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
        if (isRemoteUI()) {
            sendCoreCommand("LOAD");
            return;
        }

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
        togglePin.setEnabled(!isRemoteUI());

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
        clear.setEnabled(!isRemoteUI());

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

        loadButton.addActionListener(e -> {
            if (isRemoteUI()) sendCoreCommand("LOAD");
            else loadTasks();
        });

        stepButton.addActionListener(e -> {
            if (isRemoteUI()) sendCoreCommand("STEP");
            else executeStep();
        });


runButton.addActionListener(e -> {
    if (isRemoteUI()) {
        runButton.setEnabled(false);
        stepButton.setEnabled(false);

        log("========== RUN STARTED ==========");

        new javax.swing.SwingWorker<Void, String>() {

            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 0; i < 1000; i++) {
                    String response = uiIpc.sendCommand("STEP");

                    if (response == null) {
                        publish("IPC returned no response.");
                        break;
                    }

                    publish(response);

                    if (!response.startsWith("RESULT: STEP_COMPLETE")) {
                        break;
                    }

                    // Stop when the Core reports execution has halted.
                    if (response.matches(".*\\bhalted=1\\b.*")) {
                        publish("RUN_LOOP_COMPLETED");
                        break;
                    }
                }

                return null;
            }

            @Override
            protected void process(java.util.List<String> responses) {
                for (String response : responses) {
                    if ("RUN_LOOP_COMPLETED".equals(response)) {
                        log("========== EXECUTION COMPLETED ==========");
                        continue;
                    }

                    if (response.startsWith("RESULT: STEP_COMPLETE")) {
                        showRemoteResponse(response);

                        log("========== STEP EXECUTION ==========");
                        log("Task        : " +
                                extractField(response, "executedTask"));
                        log("Executed PC : " +
                                extractField(response, "executedPC"));
                        log("Instruction : " +
                                extractField(response, "instruction"));
                        log("W Register  : " +
                                extractField(response, "executedW"));
                        log("Next PC     : " +
                                extractField(response, "nextPC"));
                        log("====================================");
                    } else {
                        log("RUN stopped: " + response);
                    }
                }
            }

            @Override
            protected void done() {
                try {
                    get();
                } catch (Exception ex) {
                    log("RUN failed: " + ex.getMessage());
                } finally {
                    runButton.setEnabled(isLoaded);
                    stepButton.setEnabled(isLoaded);
                }
            }
        }.execute();

    } else {
        toggleRun();
    }
});



        resetButton.addActionListener(e -> {
            if (isRemoteUI()) sendCoreCommand("RESET");
            else resetSimulator();
        });


        // Week 3 actions

        enqueueButton.addActionListener(e -> {
            if (isRemoteUI()) {
                String input = JOptionPane.showInputDialog(this, "Queue value:", "10");
                if (input != null) {
                    try {
                        int value = Integer.parseInt(input.trim());
                        sendCoreCommand("ENQUEUE " + value);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Enter a valid integer.");
                    }
                }
            } else {
                enqueueTestValue();
            }
        });

        dequeueButton.addActionListener(e -> {
            if (isRemoteUI()) sendCoreCommand("DEQUEUE");
            else dequeueValue();
        });


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
        gpioButton.setEnabled(!isRemoteUI());
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
        if (isRemoteUI()) {
            sendCoreCommand("STEP");
            return;
        }

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
        if (isRemoteUI()) {
            sendCoreCommand("RUN");
            return;
        }

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
        if (isRemoteUI()) {
            sendCoreCommand("RESET");
            return;
        }

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
    if (isRemoteUI()) {
        sendCoreCommand("ENQUEUE " + nextQueueValue);
        nextQueueValue += 10;
        return;
    }

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
        if (isRemoteUI()) {
            sendCoreCommand("DEQUEUE");
            return;
        }

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
        if (isRemoteUI()) {
            showRemoteResponse("INITIAL");
            return;
        }

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
            if (isRemoteUI()) {
                try {
                    String text = stackInputField.getText().trim();
                    int val = text.startsWith("0x") || text.startsWith("0X")
                            ? Integer.parseInt(text.substring(2), 16)
                            : Integer.parseInt(text);
                    sendCoreCommand("PUSH " + val);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Enter a valid integer or hex (e.g. 0x10)",
                            "Input Error", JOptionPane.WARNING_MESSAGE);
                }
                return;
            }

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
            if (isRemoteUI()) {
                sendCoreCommand("POP");
                return;
            }

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

    peekButton.setEnabled(true);

peekButton.addActionListener(e -> {
    if (isRemoteUI()) {
        sendCoreCommand("PEEK");
        return;
    }

    int sp = dataMemory.getSP();

    if (sp <= 0) {
        JOptionPane.showMessageDialog(
            this, "Stack is empty!", "Stack Peek",
            JOptionPane.INFORMATION_MESSAGE
        );
    } else {
        int topVal = dataMemory.getStackElement(sp - 1);

        JOptionPane.showMessageDialog(
            this,
            String.format("Top of Stack (Level %d): 0x%04X",
                sp - 1, topVal),
            "Stack Peek",
            JOptionPane.INFORMATION_MESSAGE
        );
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

