public class Task {

    public enum State {
        READY,
        RUNNING,
        TERMINATED
    }

    private int taskId;
    private String taskName;
    private State state;

    // =========================================================
    // PCB / SAVED CPU CONTEXT
    // =========================================================

    private int savedPC;
    private int savedW;
    private int savedSTATUS;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public Task(int taskId, String taskName, int startPC) {

        this.taskId = taskId;
        this.taskName = taskName;

        // Initial PCB values
        this.savedPC = startPC;
        this.savedW = 0;
        this.savedSTATUS = 0;

        // Every new task starts in READY state
        this.state = State.READY;
    }

    // =========================================================
    // SAVE CPU CONTEXT INTO PCB
    // =========================================================

    public void saveContext(CPU cpu) {

        if (cpu == null) {
            throw new IllegalArgumentException("CPU cannot be null");
        }

        savedPC = cpu.getPC();
        savedW = cpu.getW();
        savedSTATUS = cpu.getSTATUS();
    }

    // =========================================================
    // RESTORE PCB CONTEXT INTO CPU
    // =========================================================

    public void restoreContext(CPU cpu) {

        if (cpu == null) {
            throw new IllegalArgumentException("CPU cannot be null");
        }

        cpu.setPC(savedPC);
        cpu.setW(savedW);
        cpu.setSTATUS(savedSTATUS);
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
        this.state = state;
    }

    // =========================================================
    // PCB INFORMATION
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
