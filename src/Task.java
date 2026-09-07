public class Task {
    public enum State {
        READY, RUNNING, TERMINATED
    }

    private int taskId;
    private String taskName;
    private State state;

    // Saved CPU Context (The "Notepad")
    private int savedPC;
    private int savedW;
    private int savedSTATUS;

    public Task(int taskId, String taskName, int startPC) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.savedPC = startPC;
        this.savedW = 0;
        this.savedSTATUS = 0;
        this.state = State.READY;
    }

    // Save current CPU values into this task's context
    public void saveContext(CPU cpu) {
        this.savedPC = cpu.getPC();
        this.savedW = cpu.getW();
        this.savedSTATUS = cpu.getSTATUS();
    }

    // Restore this task's saved context back into CPU
    public void restoreContext(CPU cpu) {
        cpu.setPC(this.savedPC);
        cpu.setW(this.savedW);
        cpu.setSTATUS(this.savedSTATUS);
    }

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

    public int getSavedPC() {
        return savedPC;
    }

    public int getSavedW() {
        return savedW;
    }
}
