import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Entry point for the separate UI Process.
 * Starts the existing Core Process through CoreIPCClient and opens the GUI
 * in IPC mode.
 */
public final class UIProcess {

    private UIProcess() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                CoreIPCClient ipc = new CoreIPCClient();
                SimulatorFrame frame = new SimulatorFrame(ipc);
                frame.setTitle("PIC16F72 Simulator - UI Process");

                frame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        ipc.close();
                    }
                });

                frame.setVisible(true);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        null,
                        "Unable to start the UI/Core connection.\\n"
                                + ex.getMessage()
                                + "\\n\\nCompile from the project root using:\\n"
                                + "javac -encoding UTF-8 -d out src\\*.java",
                        "UI Process Startup Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}

