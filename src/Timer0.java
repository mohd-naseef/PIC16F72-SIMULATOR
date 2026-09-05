// Simulates a hardware timer that counts up and overflows
public class Timer0 {
    public int counter = 250;        // Starts near 255 for quick testing
    public boolean overflow = false; // Interrupt flag for timer

    // Called on every clock cycle/step
    public void tick() {
        counter++;
        if (counter > 255) {         // 8-bit limit reached 
            counter = 0;
            overflow = true;         // Set interrupt flag
            System.out.println("[TIMER0] Overflow reached! Timer flag set.");
        }
    }

    public void clearOverflow() {
        overflow = false;
    }

    public static void main(String[] args) {
        Timer0 timer = new Timer0();

        System.out.println("Starting count at: " + timer.counter);

        // Run 8 clock ticks to watch it cross 255 and roll over to 0
        for (int step = 1; step <= 8; step++) {
            timer.tick();
            System.out.println("Tick " + step + " -> Counter: " + timer.counter + " | Overflow flag: " + timer.overflow);

            // Simulate the CPU acknowledging and clearing the interrupt
            if (timer.overflow) {
                timer.clearOverflow();
                System.out.println("--> CPU handled and cleared the overflow flag.");
            }
        }
    }
}