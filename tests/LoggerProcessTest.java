import java.io.File;

public class LoggerProcessTest {
    public static void main(String[] args) {
        System.out.println("[Test] Starting LoggerProcess IPC Test...");

        // 1. Start the LoggerProcess in a background thread
        Thread loggerThread = new Thread(() -> LoggerProcess.main(new String[]{}));
        loggerThread.setDaemon(true);
        loggerThread.start();

        try {
            // Wait briefly for the server socket to bind
            Thread.sleep(1000);

            // 2. Instantiate the LogClient and send various log levels
            LogClient client = new LogClient("localhost", 9999);

            client.info("Core process initialized successfully.");
            client.exec("PC=0x0000, Instruction=MOVLW 0x25, W=0x25");
            client.debug("Stack depth is currently 1.");
            client.warn("Timer0 prescaler overflow detected.");
            client.error("Memory access violation at 0x1FFF.");

            // Allow messages to transmit and flush to file
            Thread.sleep(1000);
            client.close();

            // 3. Verify that the log directory and file were created
            File logFile = new File("logs/simulator_execution.log");
            if (logFile.exists() && logFile.length() > 0) {
                System.out.println("\n[Test] SUCCESS: Log file was created and contains data.");
            } else {
                System.err.println("\n[Test] FAILURE: Log file was not created or is empty.");
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("[Test] LoggerProcessTest finished.");
        System.exit(0);
    }
}
