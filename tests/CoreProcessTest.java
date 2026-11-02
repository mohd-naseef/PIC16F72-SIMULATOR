public class CoreProcessTest {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("     CORE PROCESS TEST");
        System.out.println("=================================");

        // Create the Core Process
        CoreProcess core = new CoreProcess();


        // =====================================================
        // MEMORY TEST
        // =====================================================

        System.out.println("\n--- Memory Test ---");

        // Write value 50 to memory address 20
        core.writeMemory(20, 50);

        // Read the value back
        int memoryValue = core.readMemory(20);

        System.out.println("Written value : 50");
        System.out.println("Read value    : " + memoryValue);

        if (memoryValue == 50) {
            System.out.println("PASS: Memory");
        } else {
            System.out.println("FAIL: Memory");
        }


        // =====================================================
        // STACK TEST
        // =====================================================

        System.out.println("\n--- Stack Test ---");

        // Push value onto the stack
        core.pushStack(100);

        // Pop the value from the stack
        int stackValue = core.popStack();

        System.out.println("Pushed value : 100");
        System.out.println("Popped value : " + stackValue);

        if (stackValue == 100) {
            System.out.println("PASS: Stack");
        } else {
            System.out.println("FAIL: Stack");
        }


        // =====================================================
        // FIFO QUEUE TEST
        // =====================================================

        System.out.println("\n--- FIFO Queue Test ---");

        // Add three values to the queue
        core.enqueue(10);
        core.enqueue(20);
        core.enqueue(30);

        // Remove values from the queue
        int first = core.dequeue();
        int second = core.dequeue();
        int third = core.dequeue();

        System.out.println("Enqueued : 10, 20, 30");

        System.out.println("Dequeued : "
                + first + ", "
                + second + ", "
                + third);

        // Check FIFO ordering
        if (first == 10 && second == 20 && third == 30) {
            System.out.println("PASS: FIFO Queue");
        } else {
            System.out.println("FAIL: FIFO Queue");
        }


        // =====================================================
        // CPU AND SCHEDULER TEST
        // =====================================================

        System.out.println("\n--- CPU and Scheduler Test ---");

        // Clear program memory before loading test program
        core.getProgramMemory().clear();

        // Load MOVLW 10
        core.getProgramMemory().addInstruction(
                new Instruction("MOVLW", 10)
        );

        // Load ADDLW 5
        core.getProgramMemory().addInstruction(
                new Instruction("ADDLW", 5)
        );

        // Load SLEEP
        core.getProgramMemory().addInstruction(
                new Instruction("SLEEP", 0)
        );


        // Create a task for the CPU test
        Task task = new Task(
                1,
                "CPU Test Task",
                0
        );

        // Add the task to the scheduler
        core.getScheduler().addTask(task);


        // Execute first instruction: MOVLW 10
        core.getScheduler().step(core.getCPU());

        System.out.println("After MOVLW:");
        System.out.println("W = " + core.getCPU().getW());


        // Execute second instruction: ADDLW 5
        core.getScheduler().step(core.getCPU());

        System.out.println("After ADDLW:");
        System.out.println("W = " + core.getCPU().getW());


        // Check CPU result
        if (core.getCPU().getW() == 15) {
            System.out.println("PASS: CPU Execution");
        } else {
            System.out.println("FAIL: CPU Execution");
        }


        // =====================================================
        // FINAL RESULT
        // =====================================================

        System.out.println("\n=================================");
        System.out.println("       CORE TEST COMPLETED");
        System.out.println("=================================");
    }
}
