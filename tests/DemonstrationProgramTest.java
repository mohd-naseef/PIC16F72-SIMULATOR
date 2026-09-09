import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DemonstrationProgramTest {

    @Test
    @DisplayName("Executes complete demonstration program inside Scheduler and Task")
    public void testDemonstrationProgram() {
        ProgramMemory pm = new ProgramMemory();
        DataMemory dm = new DataMemory();

        // 8-instruction demonstration routine from Main.java
        pm.addInstruction(new Instruction("MOVLW", 10)); // PC 0
        pm.addInstruction(new Instruction("MOVWF", 20)); // PC 1
        pm.addInstruction(new Instruction("SUBLW", 15)); // PC 2
        pm.addInstruction(new Instruction("ANDLW", 3));  // PC 3
        pm.addInstruction(new Instruction("MOVLW", 5));  // PC 4
        pm.addInstruction(new Instruction("ADDLW", 3));  // PC 5
        pm.addInstruction(new Instruction("INCF", 20));  // PC 6
        pm.addInstruction(new Instruction("GOTO", 9));   // PC 7 -> jumps to 9
        pm.addInstruction(new Instruction("MOVLW", 99)); // PC 8 -> skipped
        pm.addInstruction(new Instruction("SLEEP", 0));  // PC 9 -> halts

        CPU cpu = new CPU(pm, dm);
        Scheduler scheduler = new Scheduler(4); // Time quantum of 4

        Task demoTask = new Task(1, "DemoTask", 0);
        scheduler.addTask(demoTask);

        // Verify initial state
        assertEquals(Task.State.READY, demoTask.getState());

        // Step through scheduler until execution completes
        int maxSteps = 25;
        int count = 0;
        while (!cpu.isHalted() && count < maxSteps) {
            scheduler.step(cpu);
            count++;
        }

        // Verify Task state and final register/memory states
        assertEquals(Task.State.TERMINATED, demoTask.getState());
        assertEquals(8, cpu.getW(), "W register should equal 8 (GOTO skips MOVLW 99)");
        assertEquals(11, dm.read(20), "RAM at address 20 should be 11 (10 stored then incremented)");
    }
}