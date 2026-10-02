public class Task {

    public enum State {
        READY,
        RUNNING,
        TERMINATED
    }

    private int taskId;
    private String taskName;
    private State state;

    // PCB / Saved CPU Context
    private int savedPC;
    private int savedW;
    private int savedSTATUS;

    // Constructor used by the existing simulator
    public Task(int taskId, String taskName, int startPC) {
        this(taskId, taskName, startPC, 0, 0);
    }

    // Constructor allowing an initial CPU context
    public Task(int taskId, String taskName,
                int startPC, int initialW, int initialSTATUS) {

        if (taskId < 0) {
            throw new IllegalArgumentException("Task ID cannot be negative");
        }

        if (taskName == null || taskName.trim().isEmpty()) {
            throw new IllegalArgumentException("Task name cannot be empty");
        }

        if (startPC < 0) {
            throw new IllegalArgumentException("Start PC cannot be negative");
        }

        this.taskId = taskId;
        this.taskName = taskName;
        this.savedPC = startPC;
        this.savedW = initialW & 0xFF;
        this.savedSTATUS = initialSTATUS & 0xFF;

        // Every newly created task starts in READY state
        this.state = State.READY;
    }

    // =========================================================
    // PCB CONTEXT SAVE
    // =========================================================

    public void saveContext(CPU cpu) {

        if (cpu == null) {
            throw new IllegalArgumentException("CPU cannot be null");
        }

        this.savedPC = cpu.getPC();
        this.savedW = cpu.getW();
        this.savedSTATUS = cpu.getSTATUS();
    }

    // =========================================================
    // PCB CONTEXT RESTORE
    // =========================================================

    public void restoreContext(CPU cpu) {

        if (cpu == null) {
            throw new IllegalArgumentException("CPU cannot be null");
        }

        cpu.setPC(this.savedPC);
        cpu.setW(this.savedW);
        cpu.setSTATUS(this.savedSTATUS);
    }

    // =========================================================
    // TASK INFORMATION
    // =========================================================

    public int getTaskId() {
        return taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {

        if (state == null) {
            throw new IllegalArgumentException("Task state cannot be null");
        }

        this.state = state;
    }

    // =========================================================
    // SAVED PCB VALUES
    // =========================================================

    public int getSavedPC() {
        return savedPC;
    }

    public int getSavedW() {
        return savedW;
    }

    public int getSavedSTATUS() {
        return savedSTATUS;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskId=" + taskId +
                ", taskName='" + taskName + '\'' +
                ", state=" + state +
                ", savedPC=" + savedPC +
                ", savedW=" + savedW +
                ", savedSTATUS=" + savedSTATUS +
                '}';
    }
}