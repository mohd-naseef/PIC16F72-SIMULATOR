import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/** UI-side client using Java process stdin/stdout pipes for UI <-> Core IPC. */
public class CoreIPCClient implements AutoCloseable {
    private final Process coreProcess;
    private final PrintWriter coreInput;
    private final BufferedReader coreOutput;
    private boolean closed;

    public CoreIPCClient() throws IOException {
        String javaExecutable = System.getProperty("java.home") + java.io.File.separator + "bin"
                + java.io.File.separator + (isWindows() ? "java.exe" : "java");
        ProcessBuilder builder = new ProcessBuilder(
                javaExecutable, "-cp", System.getProperty("java.class.path"), "CoreProcessMain");
        builder.redirectError(ProcessBuilder.Redirect.INHERIT);
        coreProcess = builder.start();
        coreInput = new PrintWriter(coreProcess.getOutputStream(), true, StandardCharsets.UTF_8);
        coreOutput = new BufferedReader(new InputStreamReader(coreProcess.getInputStream(), StandardCharsets.UTF_8));
    }

    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }

    public synchronized String sendCommand(String command) throws IOException {
        if (closed) throw new IOException("Core IPC connection is closed");
        coreInput.println(command);
        if (coreInput.checkError()) throw new IOException("Could not write command to Core process");
        String response = coreOutput.readLine();
        if (response == null) throw new IOException("Core process closed its response pipe");
        return response;
    }

    @Override public synchronized void close() {
        if (closed) return;
        try {
            coreInput.println("EXIT");
            coreOutput.readLine();
        } catch (Exception ignored) { }
        closed = true;
        coreInput.close();
        try { coreOutput.close(); } catch (IOException ignored) { }
        try {
            if (!coreProcess.waitFor(2, java.util.concurrent.TimeUnit.SECONDS)) coreProcess.destroyForcibly();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            coreProcess.destroyForcibly();
        }
    }
}