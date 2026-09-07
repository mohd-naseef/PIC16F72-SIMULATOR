import java.util.LinkedList;
import java.util.Queue;

public class Scheduler {
    private Queue<Task> readyQueue;
    private Task currentTask;
    private int timeQuantum;     // How many instructions run before switching
    private int instructionsRun;  // Counter for the current time slice

    public Scheduler(int timeQuantum) {
        this.readyQueue = new LinkedList<>();
        this.timeQuantum = timeQuantum;
        this.instructionsRun = 0;
        this.currentTask = null;
    }

    public void addTask(Task task) {
        readyQueue.add(task);
        System.out.println("[SCHEDULER] Task added: " + task.getTaskName() + " (Start PC: " + task.getSavedPC() + ")");
    }

    public Task getCurrentTask() {
        return currentTask;
    }

    /**
     * Executes one instruction step using the CPU, then updates scheduling.
     */
    public void step(CPU cpu) {
        // If nothing is currently running, schedule the first available task
        if (currentTask == null) {
            if (readyQueue.isEmpty()) {
                System.out.println("[SCHEDULER] No tasks available to run.");
                return;
            }
            switchToNextTask(cpu);
        }

        // Execute one instruction cycle: FETCH -> DECODE -> EXECUTE
        Instruction instr = cpu.fetch();
        if (instr != null) {
            String op = cpu.decode();
            cpu.execute();
            instructionsRun++;

            System.out.println("[" + currentTask.getTaskName() + "] Executed: " + op 
                + " | W=" + cpu.getW() + " | PC=" + cpu.getPC() 
                + " (Slice: " + instructionsRun + "/" + timeQuantum + ")");
        }

        // Check if current task finished (e.g. hit SLEEP or out of instructions)
        if (cpu.isHalted()) {
            System.out.println("[SCHEDULER] " + currentTask.getTaskName() + " has completed execution.");
            currentTask.setState(Task.State.TERMINATED);
            currentTask = null;
            instructionsRun = 0;
            // Load next task if any are left
            if (!readyQueue.isEmpty()) {
                switchToNextTask(cpu);
            }
            return;
        }

        // Context switch when the time quantum expires
        if (instructionsRun >= timeQuantum) {
            System.out.println("\n--- [CONTEXT SWITCH TRIGGERED] Time quantum reached ---");
            contextSwitch(cpu);
        }
    }

    private void contextSwitch(CPU cpu) {
        if (currentTask != null) {
            currentTask.saveContext(cpu);
            currentTask.setState(Task.State.READY);
            readyQueue.add(currentTask);
            System.out.println("[SAVED CONTEXT] " + currentTask.getTaskName() 
                + " -> Saved PC: " + currentTask.getSavedPC() 
                + ", W: " + currentTask.getSavedW());
        }
        switchToNextTask(cpu);
    }

    private void switchToNextTask(CPU cpu) {
        currentTask = readyQueue.poll();
        if (currentTask != null) {
            currentTask.setState(Task.State.RUNNING);
            currentTask.restoreContext(cpu);
            instructionsRun = 0;
            System.out.println("[RESTORED CONTEXT] Switched to " + currentTask.getTaskName() 
                + " -> Restored PC: " + cpu.getPC() 
                + ", W: " + cpu.getW() + "\n");
        }
    }
}
