import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CoreProcessMain {

    public static void main(String[] args) {

        System.err.println(
                "================================="
        );

        System.err.println(
                "        CORE PROCESS STARTED"
        );

        System.err.println(
                "================================="
        );


        // Create Core
        CoreProcess core =
                new CoreProcess();


        // Create thread manager
        CoreThreads threads =
                new CoreThreads(core);


        // Create threads
        Thread ipcThread =
                threads.createIPCThread();

        Thread cpuThread =
                threads.createCPUThread();

        Thread loggerThread =
                threads.createLoggerThread();


        // Start threads
        ipcThread.start();
        cpuThread.start();
        loggerThread.start();


        System.err.println(
                "Core is ready."
        );


        try {

            BufferedReader reader =
                    new BufferedReader(
                            new InputStreamReader(
                                    System.in
                            )
                    );


            String command;


            while ((command = reader.readLine()) != null) {

                command = command.trim();


                if (command.isEmpty()) {
                    continue;
                }


                System.err.println(
                        "MAIN received: " + command
                );


                // Give command to IPC thread
                threads.addCommand(command);


                // Wait for CPU result
                String response =
                        threads.getResponse();


                // ONLY this goes through the pipe
                System.out.println(response);
                System.out.flush();


                // Stop after EXIT
                if (command.equalsIgnoreCase("EXIT")) {
                    break;
                }
            }


            // Wait for threads
            ipcThread.join();
            cpuThread.join();
            loggerThread.join();


        } catch (Exception e) {

            System.err.println(
                    "Core Error: "
                    + e.getMessage()
            );
        }


        System.err.println(
                "Core Process stopped."
        );
    }
}