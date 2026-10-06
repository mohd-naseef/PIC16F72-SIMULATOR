import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class CoreIPCClient {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("       UI / IPC CLIENT");
        System.out.println("=================================");

        try {

            // Start Core Process
            ProcessBuilder processBuilder =
                    new ProcessBuilder(
                            "java",
                            "-cp",
                            "out",
                            "CoreProcessMain"
                    );

            Process coreProcess =
                    processBuilder.start();

            // Pipe: UI -> Core
            PrintWriter coreInput =
                    new PrintWriter(
                            coreProcess.getOutputStream(),
                            true
                    );

            // Pipe: Core -> UI
            BufferedReader coreOutput =
                    new BufferedReader(
                            new InputStreamReader(
                                    coreProcess.getInputStream()
                            )
                    );

            System.out.println("Core Process started.");


            // =================================================
            // FIFO QUEUE TEST
            // =================================================

            sendCommand(
                    coreInput,
                    coreOutput,
                    "ENQUEUE 10"
            );

            sendCommand(
                    coreInput,
                    coreOutput,
                    "ENQUEUE 20"
            );

            sendCommand(
                    coreInput,
                    coreOutput,
                    "ENQUEUE 30"
            );

            sendCommand(
                    coreInput,
                    coreOutput,
                    "DEQUEUE"
            );


            // =================================================
            // MEMORY TEST
            // =================================================

            sendCommand(
                    coreInput,
                    coreOutput,
                    "WRITE 20 50"
            );

            sendCommand(
                    coreInput,
                    coreOutput,
                    "READ 20"
            );


            // =================================================
            // STACK TEST
            // =================================================

            sendCommand(
                    coreInput,
                    coreOutput,
                    "PUSH 100"
            );

            sendCommand(
                    coreInput,
                    coreOutput,
                    "POP"
            );


            // =================================================
            // STOP CORE
            // =================================================

            sendCommand(
                    coreInput,
                    coreOutput,
                    "EXIT"
            );

            coreInput.close();

            // Wait for Core Process to finish
            coreProcess.waitFor();

            System.out.println();
            System.out.println("Core Process stopped.");
            System.out.println("=================================");
            System.out.println("       IPC TEST COMPLETED");
            System.out.println("=================================");

        } catch (Exception e) {

            System.out.println(
                    "IPC Error: " + e.getMessage()
            );
        }
    }


    // =========================================================
    // SEND COMMAND TO CORE
    // =========================================================

    private static void sendCommand(
            PrintWriter coreInput,
            BufferedReader coreOutput,
            String command) {

        try {

            System.out.println();
            System.out.println(
                    "Sending: " + command
            );

            // Send command through pipe
            coreInput.println(command);

            // Read Core response
            String response =
                    coreOutput.readLine();

            System.out.println(
                    "Core Response: " + response
            );

        } catch (Exception e) {

            System.out.println(
                    "Communication Error: "
                    + e.getMessage()
            );
        }
    }
}