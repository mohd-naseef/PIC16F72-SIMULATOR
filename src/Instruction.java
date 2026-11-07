public class Instruction {

    private final String opcode;
    private final int operand;
    private final int secondOperand;
    private final boolean hasSecondOperand;

    /** Creates a traditional one-operand instruction. */
    public Instruction(String opcode, int operand) {
        this.opcode = opcode;
        this.operand = operand;
        this.secondOperand = 0;
        this.hasSecondOperand = false;
    }

    /** Creates a two-operand simulator instruction (HENC, HDEC or FLIP). */
    public Instruction(String opcode, int operand, int secondOperand) {
        this.opcode = opcode;
        this.operand = operand;
        this.secondOperand = secondOperand;
        this.hasSecondOperand = true;
    }

    public String getOpcode() {
        return opcode;
    }

    public int getOperand() {
        return operand;
    }

    public int getSecondOperand() {
        return secondOperand;
    }

    public boolean hasSecondOperand() {
        return hasSecondOperand;
    }

    @Override
    public String toString() {
        if (hasSecondOperand) {
            return opcode + " " + operand + "," + secondOperand;
        }
        return opcode + " " + operand;
    }
}
