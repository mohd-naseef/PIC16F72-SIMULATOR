public class CPU {

    private int W = 0;
    private ProgramCounter programCounter;
    private int STATUS = 0;

    private ProgramMemory programMemory;
    private DataMemory dataMemory;
    private Instruction currentInstruction;

    // STATUS flag bits
    private static final int Z_FLAG = 2;   // Zero
    private static final int DC_FLAG = 1;  // Digit Carry
    private static final int C_FLAG = 0;   // Carry

    private boolean halted = false;
    private String lastExecutionMessage = "";

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public CPU(ProgramMemory programMemory, DataMemory dataMemory) {

        this.programMemory = programMemory;
        this.dataMemory = dataMemory;

        programCounter = new ProgramCounter();
    }

    // =========================================================
    // W REGISTER
    // =========================================================

    public int getW() {
        return W;
    }

    public void setW(int value) {
        W = value & 0xFF;
    }

    // =========================================================
    // PROGRAM COUNTER
    // =========================================================

    public int getPC() {
        return programCounter.getPC();
    }

    public void setPC(int value) {
        programCounter.setPC(value);
    }

    // =========================================================
    // STATUS REGISTER
    // =========================================================

    public int getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(int value) {
        STATUS = value & 0xFF;
    }

    // =========================================================
    // CPU STATE
    // =========================================================

    public boolean isHalted() {
        return halted;
    }

    /** Returns the most recent Hamming instruction result, if any. */
    public String getLastExecutionMessage() {
        return lastExecutionMessage;
    }

    /*
     * Used by the Scheduler when another task is selected.
     *
     * A previous task may have executed SLEEP, which sets
     * halted = true. The next task must be allowed to execute.
     */
    public void resume() {
        halted = false;
    }

    // =========================================================
    // FETCH
    // =========================================================

    public Instruction fetch() {

        int pc = programCounter.getPC();

        // PC outside program memory means execution has finished
        if (pc < 0 || pc >= programMemory.size()) {
            halted = true;
            return null;
        }

        currentInstruction = programMemory.getInstruction(pc);

        // PC points to next instruction
        programCounter.increment();

        return currentInstruction;
    }

    public Instruction getCurrentInstruction() {
        return currentInstruction;
    }

    // =========================================================
    // DECODE
    // =========================================================

    public String decode() {

        if (currentInstruction == null) {
            return null;
        }

        return currentInstruction.getOpcode();
    }

    // =========================================================
    // EXECUTE
    // =========================================================

    public void execute() {

        if (currentInstruction == null || halted) {
            return;
        }

        String opcode = currentInstruction.getOpcode();
        int operand = currentInstruction.getOperand();
        int operand2 = currentInstruction.getSecondOperand();
        lastExecutionMessage = "";

        switch (opcode) {

            // -------------------------------------------------
            // DATA TRANSFER
            // -------------------------------------------------

            case "MOVLW":
                W = operand & 0xFF;
                break;

            case "MOVWF":
                dataMemory.write(operand, W);
                break;

            // -------------------------------------------------
            // ARITHMETIC
            // -------------------------------------------------

            case "ADDLW": {

                int result = W + operand;

                updateCarryFlags(W, operand, result);

                W = result & 0xFF;

                updateZeroFlag(W);

                break;
            }

            case "SUBLW": {

                int result = operand - W;

                // PIC-style carry:
                // no borrow = carry set
                if (operand >= W) {
                    setFlag(C_FLAG, true);
                } else {
                    setFlag(C_FLAG, false);
                }

                W = result & 0xFF;

                updateZeroFlag(W);

                break;
            }

            // -------------------------------------------------
            // LOGICAL
            // -------------------------------------------------

            case "ANDLW":

                W = W & operand;
                W = W & 0xFF;

                updateZeroFlag(W);

                break;

            // -------------------------------------------------
            // INCREMENT
            // -------------------------------------------------

            case "INCF": {

                int address = operand;

                int value = dataMemory.read(address);

                int result = (value + 1) & 0xFF;

                dataMemory.write(address, result);

                updateZeroFlag(result);

                break;
            }

            // -------------------------------------------------
            // CONTROL FLOW
            // -------------------------------------------------

            case "GOTO":

                programCounter.jump(operand);

                break;

            // -------------------------------------------------
            // WEEK 5: HAMMING (12,8) SINGLE-BIT CORRECTION
            // -------------------------------------------------

            case "HENC": {
                int dataAddress = operand & 0xFF;
                int codeAddress = operand2 & 0xFF;
                int codeword = Hamming128.encode(dataMemory.read(dataAddress));
                writeHammingWord(codeAddress, codeword);
                lastExecutionMessage = String.format(
                        "Hamming encoded RAM[0x%02X] into 12-bit code 0x%03X at RAM[0x%02X..0x%02X].",
                        dataAddress, codeword, codeAddress, (codeAddress + 1) & 0xFF);
                break;
            }

            case "FLIP": {
                int codeAddress = operand & 0xFF;
                int bitIndex = operand2;
                int codeword = readHammingWord(codeAddress);
                int damaged = Hamming128.flip(codeword, bitIndex);
                writeHammingWord(codeAddress, damaged);
                lastExecutionMessage = String.format(
                        "Injected one-bit fault at Hamming codeword bit %d (RAM address 0x%02X).",
                        bitIndex, codeAddress);
                break;
            }

            case "HDEC": {
                int dataAddress = operand & 0xFF;
                int codeAddress = operand2 & 0xFF;
                int received = readHammingWord(codeAddress);
                int syndrome = Hamming128.syndrome(received);
                int corrected = Hamming128.decode(received);
                dataMemory.write(dataAddress, corrected);
                if (syndrome >= 1 && syndrome <= 12) {
                    int correctedBit = syndrome - 1;
                    writeHammingWord(codeAddress,
                            received ^ (1 << correctedBit));
                    lastExecutionMessage = String.format(
                            "Hamming syndrome %d: corrected codeword bit %d; recovered 0x%02X.",
                            syndrome, correctedBit, corrected);
                } else if (syndrome == 0) {
                    lastExecutionMessage = String.format(
                            "No Hamming bit error detected; recovered 0x%02X.", corrected);
                } else {
                    lastExecutionMessage = String.format(
                            "Invalid Hamming syndrome %d; result is not guaranteed.", syndrome);
                }
                break;
            }

            // -------------------------------------------------
            // PROGRAM TERMINATION
            // -------------------------------------------------

            case "SLEEP":

                halted = true;

                break;

            default:

                System.out.println(
                        "Unknown instruction: " + opcode
                );
        }
    }

    private int readHammingWord(int lowAddress) {
        int low = dataMemory.read(lowAddress & 0xFF);
        int high = dataMemory.read((lowAddress + 1) & 0xFF) & 0x0F;
        return (low | (high << 8)) & 0x0FFF;
    }

    private void writeHammingWord(int lowAddress, int codeword) {
        int address = lowAddress & 0xFF;
        int word = codeword & 0x0FFF;
        dataMemory.write(address, word & 0xFF);
        dataMemory.write((address + 1) & 0xFF, (word >>> 8) & 0x0F);
    }

    // =========================================================
    // STATUS FLAGS
    // =========================================================

    public void update_Status() {

        // STATUS flags are updated during instruction execution.
        // This method represents the separate status-update stage.
    }

    private void updateZeroFlag(int value) {

        setFlag(
                Z_FLAG,
                (value & 0xFF) == 0
        );
    }

    private void updateCarryFlags(
            int a,
            int b,
            int result) {

        // Carry
        setFlag(
                C_FLAG,
                result > 0xFF
        );

        // Digit carry from lower 4 bits
        setFlag(
                DC_FLAG,
                ((a & 0x0F) + (b & 0x0F)) > 0x0F
        );
    }

    private void setFlag(
            int bit,
            boolean value) {

        if (value) {

            STATUS =
                    STATUS | (1 << bit);

        } else {

            STATUS =
                    STATUS & ~(1 << bit);
        }
    }

    // =========================================================
    // RESET
    // =========================================================

    public void reset() {

        W = 0;

        STATUS = 0;

        programCounter.reset();

        currentInstruction = null;

        halted = false;
        lastExecutionMessage = "";
    }
}
