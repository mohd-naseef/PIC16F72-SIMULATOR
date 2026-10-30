import java.io.*;
import java.net.Socket;

public class LogClient {
    private Socket socket;
    private PrintWriter out;
    private final String host;
    private final int port;

    public LogClient(String host, int port) {
        this.host = host;
        this.port = port;
        connect();
    }

    public LogClient() {
        this("localhost", 9999);
    }

    private synchronized void connect() {
        try {
            this.socket = new Socket(host, port);
            this.out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
        } catch (IOException e) {
            System.err.println("[LogClient] Could not connect to LoggerProcess: " + e.getMessage());
        }
    }

    public synchronized void log(String level, String message) {
        if (out == null) {
            connect();
        }
        if (out != null) {
            out.println(level + ":" + message);
        }
    }

    public void info(String message) {
        log("INFO", message);
    }

    public void debug(String message) {
        log("DEBUG", message);
    }

    public void exec(String message) {
        log("EXEC", message);
    }

    public void warn(String message) {
        log("WARN", message);
    }

    public void error(String message) {
        log("ERROR", message);
    }

    public synchronized void close() {
        try {
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
    }
}
