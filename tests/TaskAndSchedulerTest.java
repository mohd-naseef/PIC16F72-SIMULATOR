import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TaskAndSchedulerTest {

    private ProgramMemory pm;
    private DataMemory dm;
    private CPU cpu;

    @BeforeEach
    public void setUp() {
        pm = new ProgramMemory();
        dm = new DataMemory();
        cpu = new CPU(pm, dm);
    }

    @Test
    @DisplayName("Task: Context save and restore correctly snapshots CPU registers")
    public void testTaskContextSaveAndRestore() {
        Task task = new Task(1, "WorkerTask", 0);
        assertEquals(Task.State.READY, task.getState());

        // Simulate CPU state
        cpu.setPC(15);
        cpu.setW(0x55);
        cpu.setSTATUS(0b00000101); // C and Z flags set

        // Save context into Task
        task.saveContext(cpu);
        assertEquals(15, task.getSavedPC());
        assertEquals(0x55, task.getSavedW());

        // Wipe CPU registers
        cpu.reset();
        assertEquals(0, cpu.getPC());
        assertEquals(0, cpu.getW());
        assertEquals(0, cpu.getSTATUS());

        // Restore context back from Task
        task.restoreContext(cpu);
        assertEquals(15, cpu.getPC());
        assertEquals(0x55, cpu.getW());
        assertEquals(0b00000101, cpu.getSTATUS());
    }

    @Test
    @DisplayName("Scheduler: Preempts task and performs context switch when time quantum expires")
    public void testSchedulerTimeQuantumPreemption() {
        // Load instructions: 3 MOVLW operations
        pm.addInstruction(new Instruction("MOVLW", 1)); // PC 0
        pm.addInstruction(new Instruction("MOVLW", 2)); // PC 1
        pm.addInstruction(new Instruction("MOVLW", 3)); // PC 2

        // Quantum = 2 instructions
        Scheduler scheduler = new Scheduler(2);
        Task taskA = new Task(1, "TaskA", 0);
        scheduler.addTask(taskA);

        // Step 1: executes first instruction
        scheduler.step(cpu);
        assertEquals(Task.State.RUNNING, taskA.getState());

        // Step 2: executes second instruction -> triggers time quantum context switch
        scheduler.step(cpu);

        // After 2 steps, task context was saved, state returned to READY, and requeued
        assertEquals(Task.State.RUNNING, scheduler.getCurrentTask().getState());
        assertEquals(2, cpu.getPC());
    }
}