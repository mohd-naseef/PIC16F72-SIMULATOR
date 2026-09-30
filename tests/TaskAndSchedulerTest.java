import org.junit.jupiter.api.BeforeEach;
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

    // 1. Task creation
    @Test
    public void testTaskCreation() {

        Task task = new Task(1, "TaskA", 0);

        assertEquals(1, task.getTaskId());
        assertEquals("TaskA", task.getTaskName());
        assertEquals(Task.State.READY, task.getState());

        assertEquals(0, task.getSavedPC());
        assertEquals(0, task.getSavedW());
        assertEquals(0, task.getSavedSTATUS());
    }

    // 2. Save and restore CPU context
    @Test
    public void testContextSaveRestore() {

        Task task = new Task(1, "TaskA", 0);

        cpu.setPC(15);
        cpu.setW(0x55);
        cpu.setSTATUS(5);

        task.saveContext(cpu);

        assertEquals(15, task.getSavedPC());
        assertEquals(0x55, task.getSavedW());
        assertEquals(5, task.getSavedSTATUS());

        cpu.reset();
        task.restoreContext(cpu);

        assertEquals(15, cpu.getPC());
        assertEquals(0x55, cpu.getW());
        assertEquals(5, cpu.getSTATUS());
    }

    // 3. READY -> RUNNING
    @Test
    public void testReadyToRunning() {

        pm.addInstruction(new Instruction("MOVLW", 10));

        Scheduler scheduler = new Scheduler(2);
        Task task = new Task(1, "TaskA", 0);

        scheduler.addTask(task);

        assertEquals(1, scheduler.getReadyQueueSize());

        scheduler.step(cpu);

        assertEquals(Task.State.RUNNING, task.getState());
        assertEquals(task, scheduler.getCurrentTask());
    }

    // 4. Context switching between tasks
    @Test
    public void testContextSwitch() {

        pm.addInstruction(new Instruction("MOVLW", 10));
        pm.addInstruction(new Instruction("MOVLW", 20));

        Scheduler scheduler = new Scheduler(1);

        Task taskA = new Task(1, "TaskA", 0);
        Task taskB = new Task(2, "TaskB", 1);

        scheduler.addTask(taskA);
        scheduler.addTask(taskB);

        // Task A runs
        scheduler.step(cpu);

        assertEquals(Task.State.RUNNING, taskA.getState());
        assertEquals(10, cpu.getW());

        // Task B runs after quantum expires
        scheduler.step(cpu);

        assertEquals(Task.State.RUNNING, taskB.getState());
        assertEquals(taskB, scheduler.getCurrentTask());
    }

    // 5. SLEEP -> TERMINATED
    @Test
    public void testTaskTermination() {

        pm.addInstruction(new Instruction("SLEEP", 0));

        Scheduler scheduler = new Scheduler(2);
        Task task = new Task(1, "TaskA", 0);

        scheduler.addTask(task);
        scheduler.step(cpu);

        assertEquals(Task.State.TERMINATED, task.getState());
        assertNull(scheduler.getCurrentTask());
    }

    // 6. Multiple tasks
    @Test
    public void testMultipleTasks() {

        pm.addInstruction(new Instruction("MOVLW", 10));
        pm.addInstruction(new Instruction("SLEEP", 0));

        pm.addInstruction(new Instruction("MOVLW", 20));
        pm.addInstruction(new Instruction("SLEEP", 0));

        Scheduler scheduler = new Scheduler(1);

        Task taskA = new Task(1, "TaskA", 0);
        Task taskB = new Task(2, "TaskB", 2);

        scheduler.addTask(taskA);
        scheduler.addTask(taskB);

        assertEquals(2, scheduler.getReadyQueueSize());

        // Run until both tasks finish
        int count = 0;

        while ((scheduler.getCurrentTask() != null ||
                scheduler.hasReadyTasks()) && count < 10) {

            scheduler.step(cpu);
            count++;
        }

        assertEquals(Task.State.TERMINATED, taskA.getState());
        assertEquals(Task.State.TERMINATED, taskB.getState());

        assertNull(scheduler.getCurrentTask());
        assertEquals(0, scheduler.getReadyQueueSize());
    }
}

