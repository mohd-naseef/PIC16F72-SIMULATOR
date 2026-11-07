public class CoreThreadsWeek5IntegrationTest {
    public static void main(String[] args) throws Exception {
        CoreProcess core = new CoreProcess();
        CoreThreads threads = new CoreThreads(core);
        Thread ipc = threads.createIPCThread();
        Thread cpu = threads.createCPUThread();
        Thread logger = threads.createLoggerThread();
        ipc.start();
        cpu.start();
        logger.start();

        threads.addCommand("LOAD");
        String loaded = threads.getResponse();
        if (!loaded.startsWith("RESULT: PROGRAM_LOADED")) {
            throw new AssertionError("IPC LOAD failed: " + loaded);
        }

        threads.addCommand("EDIT 13 FLIP 64 2");
        String edited = threads.getResponse();
        if (!edited.startsWith("RESULT: INSTRUCTION_UPDATED")) {
            throw new AssertionError("IPC EDIT failed: " + edited);
        }

        threads.addCommand("RUN");
        String run = threads.getResponse();
        if (!run.startsWith("RESULT: RUN_COMPLETE")) {
            throw new AssertionError("IPC RUN failed: " + run);
        }
        if (core.readMemory(0x20) != 0x86) {
            throw new AssertionError("IPC run did not recover 0x86.");
        }

        threads.addCommand("EXIT");
        threads.getResponse();
        ipc.join(3000);
        cpu.join(3000);
        logger.join(3000);
        if (ipc.isAlive() || cpu.isAlive() || logger.isAlive()) {
            throw new AssertionError("IPC/Core/Logger threads did not stop cleanly.");
        }
        System.out.println("PASS: Core IPC LOAD, EDIT and RUN executed Week 5 Hamming demo.");
        System.out.println("PASS: RAM[0x20] recovered 0x86; all three service threads stopped.");
    }
}
