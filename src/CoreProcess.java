public class CoreProcess {

    private CPU cpu;
    private ProgramMemory programMemory;
    private DataMemory dataMemory;
    private Scheduler scheduler;

    public CoreProcess() {

        // Create program memory
        programMemory = new ProgramMemory();

        // Create data memory
        dataMemory = new DataMemory();

        // Create CPU using existing memory objects
        cpu = new CPU(programMemory, dataMemory);

        // Create scheduler
        scheduler = new Scheduler(2);

        System.err.println("Core Process initialized.");
    }

    // Add a value to the FIFO queue
    public boolean enqueue(int value) {
        return dataMemory.enqueue(value);
    }

    // Remove a value from the FIFO queue
    public int dequeue() {
        return dataMemory.dequeue();
    }

    // Write a value to memory
    public void writeMemory(int address, int value) {
        dataMemory.write(address, value);
    }

    // Read a value from memory
    public int readMemory(int address) {
        return dataMemory.read(address);
    }

    // Push a value onto the stack
    public void pushStack(int value) {
        dataMemory.push(value);
    }

    // Pop a value from the stack
    public int popStack() {
        return dataMemory.pop();
    }

    // Get the CPU
    public CPU getCPU() {
        return cpu;
    }

    // Get the program memory
    public ProgramMemory getProgramMemory() {
        return programMemory;
    }

    // Get the data memory
    public DataMemory getDataMemory() {
        return dataMemory;
    }

    // Get the scheduler
    public Scheduler getScheduler() {
        return scheduler;
    }
}