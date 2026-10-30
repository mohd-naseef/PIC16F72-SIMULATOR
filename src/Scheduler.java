
import java.util.LinkedList;
import java.util.Queue;

public class Scheduler {


    
    public static class ExecutionRecord {
        private final int pc;
        private final String taskName;
        private final String instruction;
        private final int w;
        private final int nextPC;

        public ExecutionRecord(int pc, String taskName,
                               String instruction, int w, int nextPC) {
            this.pc = pc;
            this.taskName = taskName;
            this.instruction = instruction;
            this.w = w;
            this.nextPC = nextPC;
        }

        public int getPC() { return pc; }
        public String getTaskName() { return taskName; }
        public String getInstruction() { return instruction; }
        public int getW() { return w; }
        public int getNextPC() { return nextPC; }
    }


    private final Queue<Task> readyQueue = new LinkedList<>();
    private Task currentTask;
    private final int timeQuantum;
    private int instructionsRun;

    public Scheduler(int timeQuantum) {
        if (timeQuantum <= 0) {
            throw new IllegalArgumentException("Invalid time quantum");
        }

        this.timeQuantum = timeQuantum;
        this.instructionsRun = 0;
    }

    public void addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null");
        }

        if (task.getState() == Task.State.TERMINATED) {
            throw new IllegalStateException("Task is already terminated");
        }

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

   
    // Execute one instruction and preserve its details before task switching.
    public ExecutionRecord step(CPU cpu) {
        if (cpu == null) {
            throw new IllegalArgumentException("CPU cannot be null");
        }

        if (currentTask == null) {
            if (readyQueue.isEmpty()) {
                return null;
            }
            switchTask(cpu);
        }

        // Capture the task and address that will actually execute.
        Task executingTask = currentTask;
        int pcBefore = cpu.getPC();

        Instruction instruction = cpu.fetch();

        if (instruction == null) {
            if (currentTask != null) {
                currentTask.setState(Task.State.TERMINATED);
                currentTask = null;
            }
            instructionsRun = 0;

            if (!readyQueue.isEmpty()) {
                switchTask(cpu);
            }
            return null;
        }

        cpu.decode();
        cpu.execute();
        instructionsRun++;

        // Save the execution record BEFORE a context switch changes the CPU.
        ExecutionRecord record = new ExecutionRecord(
                pcBefore,
                executingTask.getTaskName(),
                instruction.toString(),
                cpu.getW(),
                cpu.getPC()
        );

        // Task finished.
        if (cpu.isHalted()) {
            executingTask.setState(Task.State.TERMINATED);
            currentTask = null;
            instructionsRun = 0;

            if (!readyQueue.isEmpty()) {
                switchTask(cpu);
            }
            return record;
        }

        // Time quantum finished.
        if (instructionsRun >= timeQuantum) {
            contextSwitch(cpu);
        }

        return record;
    }


    private void finishCurrentTask(CPU cpu) {
        if (currentTask != null) {
            currentTask.saveContext(cpu);
            currentTask.setState(Task.State.TERMINATED);
            currentTask = null;
        }

        instructionsRun = 0;
        cpu.resume();

        if (!readyQueue.isEmpty()) {
            switchTask(cpu);
        }
    }

    private void contextSwitch(CPU cpu) {
        if (currentTask != null) {
            // Save the outgoing task's next instruction address and registers.
            currentTask.saveContext(cpu);
            currentTask.setState(Task.State.READY);
            readyQueue.add(currentTask);
            currentTask = null;
        }

        instructionsRun = 0;

        if (!readyQueue.isEmpty()) {
            switchTask(cpu);
        }
    }

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
