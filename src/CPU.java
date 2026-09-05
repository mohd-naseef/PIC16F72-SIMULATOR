public class CPU {

    private int W = 0;
    private int PC = 0;
    private int STATUS = 0;

    private ProgramMemory programMemory;
    private Instruction currentInstruction;

    public CPU(ProgramMemory programMemory) {
        this.programMemory = programMemory;
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

    // FETCH
    public Instruction fetch() {

        currentInstruction = programMemory.getInstruction(PC);

        PC++;

        return currentInstruction;
    }
    // DECODE
public String decode() {
    if (currentInstruction == null) {
        return null;
    }

    return currentInstruction.getOpcode();
}

    public Instruction getCurrentInstruction() {
        return currentInstruction;
    }

    public void reset() {
        W = 0;
        PC = 0;
        STATUS = 0;
        currentInstruction = null;
    }
}
