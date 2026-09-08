public class DataMemory {

    private static final int MEMORY_SIZE = 256;
    private static final int STACK_SIZE = 8;

    private final int[] ram = new int[MEMORY_SIZE];
    private final int[] stack = new int[STACK_SIZE];

    private int sp = 0;

    public int read(int address) {
        if (address >= 0 && address < MEMORY_SIZE) {
            return ram[address];
        }
        return 0;
    }

    public void write(int address, int value) {
        if (address >= 0 && address < MEMORY_SIZE) {
            ram[address] = value & 0xFF;
        }
    }

    public void push(int value) {
        if (sp >= STACK_SIZE) {
            throw new IllegalStateException("Stack Overflow: stack is full");
        }

        stack[sp++] = value & 0xFFFF;
    }

    public int pop() {
        if (sp <= 0) {
            throw new IllegalStateException("Stack Underflow: stack is empty");
        }

        return stack[--sp];
    }

    public int getSP() {
        return sp;
    }

    public void setSP(int sp) {
        if (sp < 0 || sp > STACK_SIZE) {
            throw new IllegalArgumentException("Invalid stack pointer");
        }

        this.sp = sp;
    }

    public void reset() {
        Arrays.fill(ram, 0);
        Arrays.fill(stack, 0);
        sp = 0;
    }
}
