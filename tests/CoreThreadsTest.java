public class CoreThreadsTest {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("       CORE THREAD TEST");
        System.out.println("=================================");

        CoreProcess core = new CoreProcess();

        CoreThreads threads =
                new CoreThreads(core);

        Thread ipc =
                threads.createIPCThread();

        Thread cpu =
                threads.createCPUThread();

        Thread logger =
                threads.createLoggerThread();


        // Start the three threads
        ipc.start();
        cpu.start();
        logger.start();


        // Send commands through IPC thread
        threads.addCommand("ENQUEUE 10");
        threads.addCommand("ENQUEUE 20");
        threads.addCommand("DEQUEUE");

        threads.addCommand("WRITE 20 50");
        threads.addCommand("READ 20");

        threads.addCommand("PUSH 100");
        threads.addCommand("POP");


        // Give the threads time to process commands
        try {

            Thread.sleep(1000);

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }


        // Stop the threads
        threads.addCommand("EXIT");


        try {

            ipc.join();
            cpu.join();
            logger.join();

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();
        }


        System.out.println("=================================");
        System.out.println("       THREAD TEST COMPLETED");
        System.out.println("=================================");
    }
}