public class GPIO {
    public int portA = 0;              
    public boolean pinTrigger = false; // Simulates pressing an external button

    // Turn on the interrupt when the button is pressed
    public void pressButton() {
        System.out.println("[GPIO] External button pressed! Setting interrupt...");
        pinTrigger = true;
    }

    public void clearPinTrigger() {
        pinTrigger = false;
    }

    public static void main(String[] args) {
        GPIO gpio = new GPIO();
        System.out.println("Initial trigger state: " + gpio.pinTrigger);
        
        gpio.pressButton();
        System.out.println("Updated trigger state: " + gpio.pinTrigger);

        gpio.clearPinTrigger();
        System.out.println("Cleared trigger state: " + gpio.pinTrigger);
    }
}
