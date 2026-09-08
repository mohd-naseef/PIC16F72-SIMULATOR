import java.util.ArrayList;

public class ProgramMemory {

    private ArrayList<Instruction> memory = new ArrayList<>();

    public void addInstruction(Instruction instruction) {
        memory.add(instruction);
    }

    public Instruction getInstruction(int address) {
        return memory.get(address);
    }

    public int size() {
        return memory.size();
    }

    public void clear() {
        memory.clear();
    }
}
