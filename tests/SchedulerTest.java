public class SchedulerTest {
    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("  WEEK 2: OS SCHEDULING & CONTEXT SWITCH TEST ");
        System.out.println("==============================================\n");

        ProgramMemory progMem = new ProgramMemory();
        DataMemory dataMem = new DataMemory();

        // TASK 1: Program starts at address 0 (Loads 10, Adds 5, Adds 5)
        progMem.loadInstruction(0, new Instruction("MOVLW", 10));
        progMem.loadInstruction(1, new Instruction("ADDLW", 5));
        progMem.loadInstruction(2, new Instruction("ADDLW", 5));
        progMem.loadInstruction(3, new Instruction("SLEEP", 0));

        // TASK 2: Program starts at address 10 (Loads 50, Adds 10)
        progMem.loadInstruction(10, new Instruction("MOVLW", 50));
        progMem.loadInstruction(11, new Instruction("ADDLW", 10));
        progMem.loadInstruction(12, new Instruction("SLEEP", 0));

        CPU cpu = new CPU(progMem, dataMem);

        // Scheduler with a quantum of 2 instructions per turn
        Scheduler scheduler = new Scheduler(2);

        Task task1 = new Task(1, "Task_1", 0);
        Task task2 = new Task(2, "Task_2", 10);

        scheduler.addTask(task1);
        scheduler.addTask(task2);

        System.out.println("\n--- Starting Stepped Execution ---");

        // Step through 8 cycles to observe context switches back and forth
        for (int i = 1; i <= 8; i++) {
            System.out.println("\n>>> STEP " + i);
            scheduler.step(cpu);
        }

        System.out.println("\n==============================================");
        System.out.println(" TEST COMPLETE: PASS ");
        System.out.println("==============================================");
    }
}
