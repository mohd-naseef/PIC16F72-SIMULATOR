import java.util.Arrays;

public class DataMemory {
    private static final int MEMORY_SIZE = 256;
    private static final int INITIAL_SP = 0xFF;

    private final int[] ram = new int[MEMORY_SIZE];
    private int sp = INITIAL_SP;

    public int read(int address) {
        if (address >= 0 && address < MEMORY_SIZE) {
            return ram[address];
        }
        return 0; // Or throw IndexOutOfBoundsException
    }

    public void write(int address, int value) {
        if (address >= 0 && address < MEMORY_SIZE) {
            ram[address] = value & 0xFF;
        }
    }

    public void push(int value) {
        if (sp < 0) {
            throw new IllegalStateException("Stack Overflow: sp is below 0x00");
        }
        ram[sp--] = value & 0xFF;
    }

    public int pop() {
        if (sp >= INITIAL_SP) {
            throw new IllegalStateException("Stack Underflow: stack is already empty");
        }
        return ram[++sp];
    }

    public int getSP() {
        return sp;
    }

    public void setSP(int sp) {
        this.sp = sp & 0xFF;
    }

    public void reset() {
        Arrays.fill(ram, 0);
        sp = INITIAL_SP;
    }
}