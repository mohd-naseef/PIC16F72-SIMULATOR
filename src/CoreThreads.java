import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class CoreThreads {

    private CoreProcess core;

    // Command from UI to IPC thread
    private BlockingQueue<String> ipcQueue =
            new LinkedBlockingQueue<>();

    // Command from IPC thread to CPU thread
    private BlockingQueue<String> cpuQueue =
            new LinkedBlockingQueue<>();

    // Result from CPU thread to UI
    private BlockingQueue<String> responseQueue =
            new LinkedBlockingQueue<>();

    // Message for Logger thread
    private BlockingQueue<String> logQueue =
            new LinkedBlockingQueue<>();

    private volatile boolean running = true;


    public CoreThreads(CoreProcess core) {
        this.core = core;
    }


    // Send command to IPC thread
    public void addCommand(String command) {
        ipcQueue.add(command);
    }


    // Get result for UI
    public String getResponse() throws InterruptedException {
        return responseQueue.take();
    }


    // =====================================================
    // IPC THREAD
    // =====================================================

    public Thread createIPCThread() {

        return new Thread(() -> {

            System.err.println("IPC Thread started.");

            try {

                while (true) {

                    String command =
                            ipcQueue.take();

                    System.err.println(
                            "IPC received: " + command
                    );


                    if (command.equalsIgnoreCase("EXIT")) {

                        cpuQueue.add("EXIT");
                        logQueue.add("EXIT");

                        responseQueue.add(
                                "RESULT: CORE_STOPPED"
                        );

                        break;
                    }


                    // Send command to CPU thread
                    cpuQueue.put(command);

                    System.err.println(
                            "IPC sent to CPU: " + command
                    );
                }

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
            }

            System.err.println("IPC Thread stopped.");

        }, "IPC-Thread");
    }


    // =====================================================
    // CPU / CORE THREAD
    // =====================================================

    public Thread createCPUThread() {

        return new Thread(() -> {

            System.err.println("CPU Thread started.");

            try {

                while (true) {

                    String command =
                            cpuQueue.take();

                    System.err.println(
                            "CPU received: " + command
                    );


                    if (command.equalsIgnoreCase("EXIT")) {
                        break;
                    }


                    executeCommand(command);
                }

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
            }

            System.err.println("CPU Thread stopped.");

        }, "CPU-Thread");
    }


    // =====================================================
    // CORE OPERATIONS
    // =====================================================

    private void executeCommand(String command) {

        try {

            String[] parts =
                    command.trim().split("\\s+");

            String operation =
                    parts[0].toUpperCase();


            // -----------------------------
            // ENQUEUE
            // -----------------------------

            if (operation.equals("ENQUEUE")) {

                int value =
                        Integer.parseInt(parts[1]);

                boolean success =
                        core.enqueue(value);


                if (success) {

                    sendResult(
                            "RESULT: ENQUEUED " + value
                    );

                    logQueue.add(
                            "ENQUEUED " + value
                    );

                } else {

                    sendResult(
                            "RESULT: QUEUE_FULL"
                    );

                    logQueue.add(
                            "QUEUE FULL"
                    );
                }

                return;
            }


            // -----------------------------
            // DEQUEUE
            // -----------------------------

            if (operation.equals("DEQUEUE")) {

                int value =
                        core.dequeue();

                sendResult(
                        "RESULT: DEQUEUED " + value
                );

                logQueue.add(
                        "DEQUEUED " + value
                );

                return;
            }


            // -----------------------------
            // WRITE
            // -----------------------------

            if (operation.equals("WRITE")) {

                int address =
                        Integer.parseInt(parts[1]);

                int value =
                        Integer.parseInt(parts[2]);


                core.writeMemory(
                        address,
                        value
                );


                sendResult(
                        "RESULT: MEMORY_WRITTEN "
                        + address + " "
                        + value
                );


                logQueue.add(
                        "MEMORY WRITE "
                        + address + " = "
                        + value
                );

                return;
            }


            // -----------------------------
            // READ
            // -----------------------------

            if (operation.equals("READ")) {

                int address =
                        Integer.parseInt(parts[1]);


                int value =
                        core.readMemory(address);


                sendResult(
                        "RESULT: MEMORY_READ "
                        + address + " "
                        + value
                );


                logQueue.add(
                        "MEMORY READ "
                        + address + " = "
                        + value
                );

                return;
            }


            // -----------------------------
            // PUSH
            // -----------------------------

            if (operation.equals("PUSH")) {

                int value =
                        Integer.parseInt(parts[1]);


                core.pushStack(value);


                sendResult(
                        "RESULT: STACK_PUSHED "
                        + value
                );


                logQueue.add(
                        "STACK PUSH " + value
                );

                return;
            }


            // -----------------------------
            // POP
            // -----------------------------

            if (operation.equals("POP")) {

                int value =
                        core.popStack();


                sendResult(
                        "RESULT: STACK_POPPED "
                        + value
                );


                logQueue.add(
                        "STACK POP " + value
                );

                return;
            }


            sendResult(
                    "RESULT: UNKNOWN_COMMAND"
            );


        } catch (Exception e) {

            sendResult(
                    "RESULT: ERROR"
            );

            logQueue.add(
                    "ERROR: " + e.getMessage()
            );
        }
    }


    // =====================================================
    // SEND RESULT
    // =====================================================

    private void sendResult(String result) {

        responseQueue.add(result);

        System.err.println(
                "CPU result: " + result
        );
    }


    // =====================================================
    // LOGGER THREAD
    // =====================================================

    public Thread createLoggerThread() {

        return new Thread(() -> {

            System.err.println(
                    "Logger Thread started."
            );

            try {

                while (true) {

                    String message =
                            logQueue.take();


                    if (message.equalsIgnoreCase("EXIT")) {
                        break;
                    }


                    System.err.println(
                            "[LOGGER] " + message
                    );
                }

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();
            }

            System.err.println(
                    "Logger Thread stopped."
            );

        }, "Logger-Thread");
    }
}