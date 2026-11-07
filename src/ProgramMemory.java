import java.util.ArrayList;

public class ProgramMemory {

    private ArrayList<Instruction> memory = new ArrayList<>();

    public void addInstruction(Instruction instruction) {
        memory.add(instruction);
    }

    public Instruction getInstruction(int address) {
        return memory.get(address);
    }

    /** Replaces an instruction at an existing program-memory address. */
    public void setInstruction(int address, Instruction instruction) {
        if (instruction == null) {
            throw new IllegalArgumentException("Instruction cannot be null");
        }
        if (address < 0 || address >= memory.size()) {
            throw new IndexOutOfBoundsException("Invalid program address: " + address);
        }
        memory.set(address, instruction);
    }

    public int size() {
        return memory.size();
    }

    public void clear() {
        memory.clear();
    }
}
