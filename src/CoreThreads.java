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
            String[] parts = command.trim().split("\\s+");
            String operation = parts[0].toUpperCase();

            switch (operation) {
                case "LOAD":
                    sendResult(core.loadProgram());
                    logQueue.add("PROGRAM LOADED");
                    return;
                case "LIST":
                    sendResult(core.getProgramListing());
                    return;
                case "STEP":
                    sendResult(core.stepProgram());
                    logQueue.add("CPU STEP");
                    return;
                case "RUN":
                    sendResult(core.runProgram());
                    logQueue.add("CPU RUN");
                    return;
                case "RESET":
                    sendResult(core.resetProgram());
                    logQueue.add("PROGRAM RESET");
                    return;
                case "STATUS":
                    sendResult(core.getStatusResponse());
                    return;
                    case "QUEUE":
    sendResult("RESULT: QUEUE " + core.getQueueStatus());
    return;
                case "ENQUEUE": {
                    requireParts(parts, 2);
                    int value = Integer.decode(parts[1]);
                    if (core.enqueue(value)) {
                        sendResult("RESULT: ENQUEUED " + value);
                        logQueue.add("ENQUEUED " + value);
                    } else {
                        sendResult("RESULT: QUEUE_FULL");
                        logQueue.add("QUEUE FULL");
                    }
                    return;
                }
                case "DEQUEUE": {
                    int value = core.dequeue();
                    sendResult("RESULT: DEQUEUED " + value);
                    logQueue.add("DEQUEUED " + value);
                    return;
                }
                case "WRITE": {
                    requireParts(parts, 3);
                    int address = Integer.decode(parts[1]);
                    int value = Integer.decode(parts[2]);
                    core.writeMemory(address, value);
                    sendResult("RESULT: MEMORY_WRITTEN " + address + " " + value);
                    logQueue.add("MEMORY WRITE " + address + " = " + value);
                    return;
                }
                case "READ": {
                    requireParts(parts, 2);
                    int address = Integer.decode(parts[1]);
                    sendResult("RESULT: MEMORY_READ " + address + " " + core.readMemory(address));
                    return;
                }
                case "PUSH": {
                    requireParts(parts, 2);
                    int value = Integer.decode(parts[1]);
                    core.pushStack(value);
                    sendResult("RESULT: STACK_PUSHED " + value);
                    logQueue.add("STACK PUSH " + value);
                    return;
                }
                case "POP": {
                    int value = core.popStack();
                    sendResult("RESULT: STACK_POPPED " + value);
                    logQueue.add("STACK POP " + value);
                    return;
                }
                case "PEEK":
                    sendResult("RESULT: STACK_TOP " + core.peekStack());
                    return;
                case "EXIT":
                    sendResult("RESULT: CORE_STOPPED");
                    return;
                default:
                    sendResult("RESULT: UNKNOWN_COMMAND " + operation);
            }
        } catch (Exception e) {
            sendResult("RESULT: ERROR " + e.getClass().getSimpleName() + " " + String.valueOf(e.getMessage()));
            logQueue.add("ERROR: " + e.getMessage());
        }
    }

    private void requireParts(String[] parts, int expected) {
        if (parts.length != expected) {
            throw new IllegalArgumentException("Expected " + (expected - 1) + " argument(s)");
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