import javax.swing.SwingUtilities;

/** Launches the graphical PIC16F72 simulator. */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimulatorFrame simulator = new SimulatorFrame();
            simulator.setVisible(true);
        });
    }
}

           
