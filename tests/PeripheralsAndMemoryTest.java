import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PeripheralsAndMemoryTest {

    // ================= DataMemory & Stack Tests =================
    @Test
    @DisplayName("DataMemory: Read, write, and stack push/pop boundary checks")
    public void testDataMemoryAndStack() {
        DataMemory dm = new DataMemory();

        // 1. RAM Read/Write masking to 8-bit
        dm.write(0x10, 0x1FF); // 0x1FF & 0xFF == 0xFF
        assertEquals(0xFF, dm.read(0x10));

        // 2. Stack Push and Pop (LIFO order)
        dm.push(0x100);
        dm.push(0x200);
        assertEquals(2, dm.getSP());
        assertEquals(0x200, dm.pop());
        assertEquals(0x100, dm.pop());
        assertEquals(0, dm.getSP());

        // 3. Stack Underflow exception
        assertThrows(IllegalStateException.class, dm::pop, "Popping an empty stack must throw underflow");

        // 4. Stack Overflow exception (STACK_SIZE is 8)
        for (int i = 0; i < 8; i++) {
            dm.push(i);
        }
        assertEquals(8, dm.getSP());
        assertThrows(IllegalStateException.class, () -> dm.push(99), "Pushing beyond 8 levels must throw overflow");
    }

    // ================= GPIO Tests =================
    @Test
    @DisplayName("GPIO: Button press latches trigger and clear clears trigger")
    public void testGPIOTrigger() {
        GPIO gpio = new GPIO();
        assertFalse(gpio.pinTrigger, "Initial pin trigger must be false");

        gpio.pressButton();
        assertTrue(gpio.pinTrigger, "Button press must set pinTrigger to true");

        gpio.clearPinTrigger();
        assertFalse(gpio.pinTrigger, "clearPinTrigger must reset pinTrigger to false");
    }

    // ================= Timer0 Tests =================
    @Test
    @DisplayName("Timer0: Ticks to 255 and sets overflow flag at 256 rollover")
    public void testTimer0Overflow() {
        Timer0 timer = new Timer0();
        timer.counter = 254;
        timer.clearOverflow();

        // Tick to 255: no overflow yet
        timer.tick();
        assertEquals(255, timer.counter);
        assertFalse(timer.overflow);

        // Tick to 256: rolls over to 0 and raises overflow
        timer.tick();
        assertEquals(0, timer.counter);
        assertTrue(timer.overflow, "Timer0 should set overflow flag when rolling over 255");

        // Clear overflow
        timer.clearOverflow();
        assertFalse(timer.overflow);
    }
}