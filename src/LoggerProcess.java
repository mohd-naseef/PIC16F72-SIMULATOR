import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoggerProcess {
    private static final int LOG_PORT = 9999;
    private static final String LOG_DIR = "logs";
    private static final String LOG_FILE = "logs/simulator_execution.log";
    private static PrintWriter fileWriter;
    private static volatile boolean running = true;

    public static void main(String[] args) {
        System.out.println("[LoggerProcess] Starting Logging Subsystem on port " + LOG_PORT + "...");

        // Ensure directory exists
        File dir = new File(LOG_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            fileWriter = new PrintWriter(new FileWriter(LOG_FILE, true), true);
            log("INIT", "Logger process initialized and listening for IPC events.");

            ServerSocket serverSocket = new ServerSocket(LOG_PORT);

            // Accept incoming connections from CoreProcess or UI
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(new LogClientHandler(clientSocket)).start();
                } catch (IOException e) {
                    if (!running) break;
                    System.err.println("[LoggerProcess] Connection accept error: " + e.getMessage());
                }
            }
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("[LoggerProcess] Fatal error: " + e.getMessage());
        } finally {
            if (fileWriter != null) {
                fileWriter.close();
            }
        }
    }

    public static synchronized void log(String level, String message) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
        String formatted = String.format("[%s] [%-5s] %s", timestamp, level, message);

        // Echo to terminal
        System.out.println(formatted);

        // Write to log file
        if (fileWriter != null) {
            fileWriter.println(formatted);
        }
    }

    private static class LogClientHandler implements Runnable {
        private final Socket socket;

        public LogClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;

                    if ("CMD:SHUTDOWN".equalsIgnoreCase(line)) {
                        log("INFO", "Shutdown command received. Terminating Logger.");
                        running = false;
                        System.exit(0);
                    }

                    int splitIdx = line.indexOf(':');
                    if (splitIdx != -1) {
                        String level = line.substring(0, splitIdx).trim();
                        String msg = line.substring(splitIdx + 1).trim();
                        log(level, msg);
                    } else {
                        log("INFO", line);
                    }
                }
            } catch (IOException e) {
                log("WARN", "Client connection closed: " + e.getMessage());
            }
        }
    }
}
