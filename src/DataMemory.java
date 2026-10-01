import java.util.Arrays;

public class DataMemory {

    private static final int MEMORY_SIZE = 256;
    private static final int STACK_SIZE = 8;

    private final int[] ram = new int[MEMORY_SIZE];
    private final int[] stack = new int[STACK_SIZE];
    private int sp = 0;

    // Week 3 FIFO Queue instance
    private final FIFOQueue queue = new FIFOQueue(8);

    // ================= MEMORY READ / WRITE =================
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

    public int getMemorySize() {
        return MEMORY_SIZE;
    }

    // ================= STACK (LIFO) =================
    public void push(int value) {
        if (sp >= STACK_SIZE) {
            throw new IllegalStateException("Stack Overflow: stack is full");
        }
        stack[sp++] = value & 0x1FFF; // 13-bit PC representation for PIC16F72
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
            throw new IllegalArgumentException("Invalid stack pointer: " + sp);
        }
        this.sp = sp;
    }

    public int getStackElement(int index) {
        if (index >= 0 && index < STACK_SIZE) {
            return stack[index];
        }
        return 0;
    }

    public int getStackSize() {
        return STACK_SIZE;
    }

    // ================= FIFO QUEUE =================
    public FIFOQueue getQueue() {
        return queue;
    }

    public boolean enqueue(int value) {
        boolean success = queue.enqueue(value);
        syncQueueToRam();
        return success;
    }

    public int dequeue() {
        int value = queue.dequeue();
        syncQueueToRam();
        return value;
    }

    /** Mirrors the 8 queue slots into RAM addresses 0x30 - 0x37 for inspection */
    private void syncQueueToRam() {
        int[] buffer = queue.getBuffer();
        for (int i = 0; i < buffer.length; i++) {
            ram[0x30 + i] = buffer[i];
        }
    }

    // ================= RESET =================
    public void reset() {
        Arrays.fill(ram, 0);
        Arrays.fill(stack, 0);
        sp = 0;
        queue.reset();
    }
}