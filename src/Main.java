public class Main {
    public static void main(String[] args) {
        DataMemory mem = new DataMemory();

        // 1. Initial State
        System.out.println("Initial SP: 0x" + Integer.toHexString(mem.getSP()).toUpperCase());

        // 2. Direct Read/Write
        mem.write(0x10, 0x42);
        System.out.println("Read addr 0x10: 0x" + Integer.toHexString(mem.read(0x10)).toUpperCase());

        // 3. Stack Push operations
        mem.push(0xAA);
        mem.push(0xBB);
        System.out.println("SP after 2 pushes: 0x" + Integer.toHexString(mem.getSP()).toUpperCase());

        // 4. Stack Pop operations (LIFO order)
        int popped1 = mem.pop();
        int popped2 = mem.pop();
        System.out.println("First pop: 0x" + Integer.toHexString(popped1).toUpperCase());
        System.out.println("Second pop: 0x" + Integer.toHexString(popped2).toUpperCase());
        System.out.println("SP after 2 pops: 0x" + Integer.toHexString(mem.getSP()).toUpperCase());

        // 5. Test Stack Underflow Exception
        try {
            mem.pop();
        } catch (IllegalStateException e) {
            System.out.println("Caught expected exception: " + e.getMessage());
        }

    ProgramMemory memory = new ProgramMemory();

    memory.addInstruction(new Instruction("MOVLW", 25));

    CPU cpu = new CPU(memory);

    cpu.fetch();
    cpu.decode();
    cpu.execute();

    System.out.println("W = " + cpu.getW());
} }
