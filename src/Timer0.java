public class Timer0 {
    public int counter = 250;        
    public boolean overflow = false; 

    public void tick() {
        counter++;
        if (counter > 255) {         
            counter = 0;
            overflow = true;         
            System.out.println("[TIMER0] Overflow reached! Timer flag set.");
        }
    }

    public void clearOverflow() {
        overflow = false;
    }

    public static void main(String[] args) {
        Timer0 timer = new Timer0();

        System.out.println("Starting count at: " + timer.counter);


        for (int step = 1; step <= 8; step++) {
            timer.tick();
            System.out.println("Tick " + step + " -> Counter: " + timer.counter + " | Overflow flag: " + timer.overflow);

            
            if (timer.overflow) {
                timer.clearOverflow();
                System.out.println("--> CPU handled and cleared the overflow flag.");
            }
        }
    }
}
