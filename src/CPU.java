public class CPU {

    private int W = 0;
    private int PC = 0;
    private int STATUS = 0;

    private ProgramMemory programMemory;
    private DataMemory dataMemory;
    private Instruction currentInstruction;

    // STATUS flag bits
    private static final int Z_FLAG = 2;  // Zero
    private static final int DC_FLAG = 1; // Digit Carry
    private static final int C_FLAG = 0;  // Carry

    private boolean halted = false;

    public CPU(ProgramMemory programMemory, DataMemory dataMemory) {
        this.programMemory = programMemory;
        this.dataMemory = dataMemory;
    }

    public int getW() {
        return W;
    }

    public void setW(int value) {
        W = value & 0xFF;
    }

    public int getPC() {
        return PC;
    }

    public void setPC(int value) {
        PC = value;
    }

    public int getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(int value) {
        STATUS = value & 0xFF;
    }

    public boolean isHalted() {
        return halted;
    }

    // ================= FETCH =================
    public Instruction fetch() {
        if (PC < 0 || PC >= programMemory.size()) {
            halted = true;
            return null;
        }

        currentInstruction = programMemory.getInstruction(PC);
        PC++;

        return currentInstruction;
    }

    public Instruction getCurrentInstruction() {
        return currentInstruction;
    }

    // ================= DECODE =================
    public String decode() {
        if (currentInstruction == null) {
            return null;
        }

        return currentInstruction.getOpcode();
    }

    // ================= EXECUTE =================
    public void execute() {

        if (currentInstruction == null || halted) {
            return;
        }

        String opcode = currentInstruction.getOpcode();
        int operand = currentInstruction.getOperand();

        switch (opcode) {

            // 1. Data Transfer
            case "MOVLW":
                W = operand & 0xFF;
                break;

            case "MOVWF":
                dataMemory.write(operand, W);
                break;

            // 2. Arithmetic
            case "ADDLW": {
                int result = W + operand;

                updateCarryFlags(W, operand, result);

                W = result & 0xFF;
                updateZeroFlag(W);
                break;
            }

            case "SUBLW": {
                int result = operand - W;

                // PIC-style carry: no borrow = carry set
                if (operand >= W) {
                    setFlag(C_FLAG, true);
                } else {
                    setFlag(C_FLAG, false);
                }

                W = result & 0xFF;
                updateZeroFlag(W);
                break;
            }

            // 3. Logical
            case "ANDLW":
                W = W & operand;
                W = W & 0xFF;
                updateZeroFlag(W);
                break;

            // 4. Increment / Decrement
            case "INCF": {
                int address = operand;
                int value = dataMemory.read(address);
                int result = (value + 1) & 0xFF;

                dataMemory.write(address, result);
                updateZeroFlag(result);
                break;
            }

            // 5. Control Flow
            case "GOTO":
                PC = operand;
                break;

            // 6. Program Termination
            case "SLEEP":
                halted = true;
                break;

            default:
                System.out.println("Unknown instruction: " + opcode);
        }
    }

    // ================= STATUS FLAGS =================

    private void updateZeroFlag(int value) {
        setFlag(Z_FLAG, (value & 0xFF) == 0);
    }

    private void updateCarryFlags(int a, int b, int result) {

        // Carry
        setFlag(C_FLAG, result > 0xFF);

        // Digit carry from lower 4 bits
        setFlag(DC_FLAG, ((a & 0x0F) + (b & 0x0F)) > 0x0F);
    }

    private void setFlag(int bit, boolean value) {

        if (value) {
            STATUS = STATUS | (1 << bit);
        } else {
            STATUS = STATUS & ~(1 << bit);
        }
    }

    // ================= RESET =================

    public void reset() {
        W = 0;
        PC = 0;
        STATUS = 0;
        currentInstruction = null;
        halted = false;
    }
}
