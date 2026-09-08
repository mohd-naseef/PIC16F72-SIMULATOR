public class SchedulerTest {
    public static void main(String[] args) {
        ProgramMemory progMem = new ProgramMemory();
        DataMemory dataMem = new DataMemory();

        // Task 1 Instructions (Address 0 to 3)
        progMem.addInstruction(new Instruction("MOVLW", 10)); // PC 0
        progMem.addInstruction(new Instruction("ADDLW", 5));  // PC 1
        progMem.addInstruction(new Instruction("ADDLW", 5));  // PC 2
        progMem.addInstruction(new Instruction("SLEEP", 0));  // PC 3

        // Task 2 Instructions (Address 4 to 6)
        progMem.addInstruction(new Instruction("MOVLW", 50)); // PC 4
        progMem.addInstruction(new Instruction("ADDLW", 10)); // PC 5
        progMem.addInstruction(new Instruction("SLEEP", 0));  // PC 6

        // Initialize CPU with program and data memory
        CPU cpu = new CPU(progMem, dataMem);

        // Initialize Scheduler with a time slice of 2 cycles
        Scheduler scheduler = new Scheduler(2);

        // Task 1: ID 1, Name "Task1", PC 0
        // Task 2: ID 2, Name "Task2", PC 4
        Task t1 = new Task(1, "Task1", 0);
        Task t2 = new Task(2, "Task2", 4);

        scheduler.addTask(t1);
        scheduler.addTask(t2);

        System.out.println("--- STARTING SCHEDULER & CONTEXT SWITCH TEST ---");

        // Run slice 1 (Task 1)
        scheduler.step(cpu);

        // Run slice 2 (Switches to Task 2)
        scheduler.step(cpu);

        // Run slice 3 (Switches back to Task 1)
        scheduler.step(cpu);

        // Run slice 4 (Switches back to Task 2)
        scheduler.step(cpu);

        System.out.println("--- TEST COMPLETE ---");
    }
}
