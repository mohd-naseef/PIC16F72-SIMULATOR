import java.util.LinkedList;
import java.util.Queue;

public class Scheduler {

    private Queue<Task> readyQueue = new LinkedList<>();
    private Task currentTask;
    private int timeQuantum;
    private int instructionsRun;

    public Scheduler(int timeQuantum) {
        if (timeQuantum <= 0)
            throw new IllegalArgumentException("Invalid time quantum");

        this.timeQuantum = timeQuantum;
    }

    // Add a task to READY queue
    public void addTask(Task task) {
        if (task == null)
            throw new IllegalArgumentException("Task cannot be null");

        if (task.getState() == Task.State.TERMINATED)
            throw new IllegalStateException("Task is already terminated");

        task.setState(Task.State.READY);
        readyQueue.add(task);
    }

    public Task getCurrentTask() {
        return currentTask;
    }

    public int getReadyQueueSize() {
        return readyQueue.size();
    }

    public boolean hasReadyTasks() {
        return !readyQueue.isEmpty();
    }

    // Execute one instruction
    public void step(CPU cpu) {

        if (cpu == null)
            throw new IllegalArgumentException("CPU cannot be null");

        // Select a task if CPU has no current task
        if (currentTask == null) {
            if (readyQueue.isEmpty())
                return;

            switchTask(cpu);
        }

        Instruction instruction = cpu.fetch();

        if (instruction != null) {
            cpu.decode();
            cpu.execute();
            instructionsRun++;
        }

        // Task finished
        if (cpu.isHalted()) {
            currentTask.setState(Task.State.TERMINATED);
            currentTask = null;
            instructionsRun = 0;

            if (!readyQueue.isEmpty())
                switchTask(cpu);

            return;
        }

        // Time quantum finished
        if (instructionsRun >= timeQuantum)
            contextSwitch(cpu);
    }

    // Save current task and select next task
    private void contextSwitch(CPU cpu) {

        if (currentTask != null) {
            currentTask.saveContext(cpu);
            currentTask.setState(Task.State.READY);
            readyQueue.add(currentTask);
        }

        switchTask(cpu);
    }

    // Select and restore next READY task
    private void switchTask(CPU cpu) {

        currentTask = readyQueue.poll();

        if (currentTask != null) {
            currentTask.setState(Task.State.RUNNING);
            currentTask.restoreContext(cpu);
            cpu.resume();
            instructionsRun = 0;
        }
    }
}
