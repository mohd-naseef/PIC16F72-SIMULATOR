public class CoreProcess {
    private CPU cpu;
    private ProgramMemory programMemory;
    private DataMemory dataMemory;
    private Scheduler scheduler;
    private boolean loaded;
    private LogClient logger;
    public CoreProcess() {
        programMemory = new ProgramMemory();
        dataMemory = new DataMemory();
        cpu = new CPU(programMemory, dataMemory);
        scheduler = new Scheduler(2);
        loaded = false;
        System.err.println("Core Process initialized.");
        this.logger = new LogClient("localhost", 9999);
        this.logger.info("CoreProcess connected to LoggerProcess successfully.");
    }

    public synchronized String loadProgram() {
        programMemory.clear();
        dataMemory.reset();
        cpu.reset();
        scheduler = new Scheduler(2);

        // Task 1: program addresses 0-4
        programMemory.addInstruction(new Instruction("MOVLW", 10));
        programMemory.addInstruction(new Instruction("MOVWF", 20));
        programMemory.addInstruction(new Instruction("ADDLW", 5));
        programMemory.addInstruction(new Instruction("SUBLW", 2));
        programMemory.addInstruction(new Instruction("SLEEP", 0));

        // Task 2: program addresses 5-9
        programMemory.addInstruction(new Instruction("MOVLW", 50));
        programMemory.addInstruction(new Instruction("ADDLW", 10));
        programMemory.addInstruction(new Instruction("ANDLW", 15));
        programMemory.addInstruction(new Instruction("INCF", 20));
        programMemory.addInstruction(new Instruction("SLEEP", 0));

        // Week 5 Hamming demonstration: addresses 10-15.
        // 0x30-0x37 are mirrored by the Week 3 FIFO, so the
        // Hamming codeword uses 0x40-0x41 to avoid that RAM region.
        programMemory.addInstruction(new Instruction("MOVLW", 0x86));
        programMemory.addInstruction(new Instruction("MOVWF", 0x20));
        programMemory.addInstruction(new Instruction("HENC", 0x20, 0x40));
        programMemory.addInstruction(new Instruction("FLIP", 0x40, 2));
        programMemory.addInstruction(new Instruction("HDEC", 0x20, 0x40));
        programMemory.addInstruction(new Instruction("SLEEP", 0));

        scheduler.addTask(new Task(1, "Task 1", 0));
        scheduler.addTask(new Task(2, "Task 2", 5));
        scheduler.addTask(new Task(3, "Week 5 Hamming", 10));
        loaded = true;
        return "RESULT: PROGRAM_LOADED count=" + programMemory.size();
    }

    // Format: RESULT: PROGRAM_LIST 0|MOVLW|10;1|MOVWF|20;...
    public synchronized String editInstruction(int address, String opcode,
                                                int operand, Integer secondOperand) {
        if (!loaded) return "RESULT: ERROR_PROGRAM_NOT_LOADED";
        if (address < 0 || address >= programMemory.size()) {
            return "RESULT: ERROR_INVALID_ADDRESS " + address;
        }
        Instruction updated = secondOperand == null
                ? new Instruction(opcode, operand)
                : new Instruction(opcode, operand, secondOperand);
        programMemory.setInstruction(address, updated);
        return "RESULT: INSTRUCTION_UPDATED address=" + address;
    }

    public synchronized String getProgramListing() {
        if (!loaded) return "RESULT: ERROR_PROGRAM_NOT_LOADED";
        StringBuilder out = new StringBuilder("RESULT: PROGRAM_LIST ");
        for (int i = 0; i < programMemory.size(); i++) {
            if (i > 0) out.append(';');
            Instruction instruction = programMemory.getInstruction(i);
            out.append(i).append('|')
               .append(instruction.getOpcode()).append('|')
               .append(instruction.getOperand());
            if (instruction.hasSecondOperand()) {
                out.append(',').append(instruction.getSecondOperand());
            }
        }
        return out.toString();
    }



public synchronized String stepProgram() {
    if (!loaded) {
        return "RESULT: ERROR_PROGRAM_NOT_LOADED";
    }

    if (scheduler.getCurrentTask() == null
            && !scheduler.hasReadyTasks()) {
        return status("PROGRAM_FINISHED");
    }

    Scheduler.ExecutionRecord record = scheduler.step(cpu);
    cpu.update_Status();
    if (logger != null) {
        logger.exec("Executed step: " + (record != null ? record.toString() : "No record"));
        if (cpu.getLastExecutionMessage() != null
                && !cpu.getLastExecutionMessage().isEmpty()) {
            logger.info(cpu.getLastExecutionMessage());
        }
    }

    if (record == null) {
        return status("PROGRAM_FINISHED");
    }

    return status("STEP_COMPLETE")
            + " executedPC=" + record.getPC()
            + " executedTask=" + record.getTaskName()
            + " instruction=" + record.getInstruction()
            + " executedW=" + record.getW()
            + " nextPC=" + record.getNextPC()
            + (cpu.getLastExecutionMessage().isEmpty()
                    ? ""
                    : " hamming=" + cpu.getLastExecutionMessage().trim().replaceAll("\\s+", "_"));
}

   


public synchronized String runProgram() {
    if (!loaded) {
        return "RESULT: ERROR_PROGRAM_NOT_LOADED";
    }

    StringBuilder trace = new StringBuilder();
    int steps = 0;
    final int maxSteps = 1000;

    while (steps < maxSteps
            && (scheduler.getCurrentTask() != null
                || scheduler.hasReadyTasks())) {

        Scheduler.ExecutionRecord record = scheduler.step(cpu);

        if (record == null) {
            break;
        }

        cpu.update_Status();

        if (logger != null && cpu.getLastExecutionMessage() != null
                && !cpu.getLastExecutionMessage().isEmpty()) {
            logger.info(cpu.getLastExecutionMessage());
        }

        if (trace.length() > 0) {
            trace.append(";");
        }

        trace.append("PC=").append(record.getPC())
             .append(",TASK=").append(record.getTaskName())
             .append(",INSTRUCTION=").append(record.getInstruction())
             .append(",W=").append(record.getW())
             .append(",NEXT_PC=").append(record.getNextPC());

        steps++;
    }

    boolean finished = scheduler.getCurrentTask() == null
            && !scheduler.hasReadyTasks();

    return status("RUN_COMPLETE")
            + " steps=" + steps
            + " allTasksFinished=" + (finished ? 1 : 0)
            + " trace=" + trace;
}

    public synchronized String resetProgram() {
        programMemory.clear();
        dataMemory.reset();
        cpu.reset();
        scheduler = new Scheduler(2);
        loaded = false;
        return status("RESET_COMPLETE");
    }

    public synchronized String getStatusResponse() {
        return status("STATUS");
    }

    private String status(String label) {
        Task task = scheduler.getCurrentTask();
        String taskName = task == null ? "NONE" : task.getTaskName();
        return "RESULT: " + label
                + " loaded=" + (loaded ? 1 : 0)
                + " pc=" + cpu.getPC()
                + " w=" + cpu.getW()
                + " status=" + cpu.getSTATUS()
                + " ram20=" + dataMemory.read(20)
                + " ram32=" + dataMemory.read(0x20)
                + " hamcode=" + (dataMemory.read(0x40)
                        | ((dataMemory.read(0x41) & 0x0F) << 8))
                + " sp=" + dataMemory.getSP()
                + " halted=" + (cpu.isHalted() ? 1 : 0)
                + " task=" + taskName;
    }

    public synchronized boolean enqueue(int value) { return dataMemory.enqueue(value); }
    public synchronized int dequeue() { return dataMemory.dequeue(); }
    public synchronized String getQueueStatus() {
    return dataMemory.getQueueStatus();
}
    public synchronized void writeMemory(int address, int value) { dataMemory.write(address, value); }
    public synchronized int readMemory(int address) { return dataMemory.read(address); }
    public synchronized void pushStack(int value) { dataMemory.push(value); }
    public synchronized int popStack() { return dataMemory.pop(); }
    public synchronized int peekStack() {
        if (dataMemory.getSP() <= 0) throw new IllegalStateException("Stack is empty");
        return dataMemory.getStackElement(dataMemory.getSP() - 1);
    }

    public CPU getCPU() { return cpu; }
    public ProgramMemory getProgramMemory() { return programMemory; }
    public DataMemory getDataMemory() { return dataMemory; }
    public Scheduler getScheduler() { return scheduler; }
    public synchronized boolean isLoaded() { return loaded; }
}
