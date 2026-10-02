# 1. GPIO (General Purpose Input / Output)
The PIC16F72  uses its physical pins to connect to buttons, lights, and sensors. These pins are grouped into PORTA , PORTB , and PORTC .  


TRIS (Tri-State Register): Sets whether a pin listens or sends.  
‣ Write 1 = Input (reads an incoming signal).  
‣ Write 0 = Output (sends voltage out).  




PORT (Input/Output Port Register): Sets or reads the actual voltage on the pin.  
‣ Write 1 = High ( 5 Volts).  
‣ Write 0 = Low ( 0 Volts).  


Reading it shows what voltage is currently at the pin.  

###  Examples:
1. To read a button on pin RB0 (Port B, Bit 0): Set TRISB (Tri-State Register B) bit 0 to 1 (Input), then read PORTB (Port B Register) bit 0.  
2. To light an LED  on pin RB1 (Port B, Bit 1): Set TRISB (Tri-State Register B) bit 1 to 0 (Output), then write 1 to PORTB (Port B Register) bit 1.  

# 2. Timer0 (The Counter)

->Timer0 is an 8-bit counter inside the chip that counts upwards from 0 to 255.  

->TMR0 (Timer 0 Register): Stores the current count number (0 to 255).  

->Overflow: When it passes 255, it rolls back around to 0.  

->T0IF (Timer 0 Interrupt Flag): Flips to 1 inside the INTCON (Interrupt Control Register) whenever the counter rolls over from 255 to 0.  

->Prescaler: A built-in divider that slows the count down (e.g., counting once every 2, 4, 8, ... up to 256 clock cycles instead of every single tick).  

###  Example:
If Timer0 is at 255 and ticks once more, TMR0 (Timer 0 Register) becomes 0 and T0IF (Timer 0 Interrupt Flag) turns to 1.  
With a 1:4 prescaler, TMR0 (Timer 0 Register) only increases by 1 after every 4 instruction cycles.  

# 3. Interrupts (Priority Service Requests)

‣ An interrupt pauses normal code to handle an urgent task immediately, then resumes where it left off.  

‣ INTCON (Interrupt Control Register): The control hub where you enable interrupts and read their flags.  

‣ GIE (Global Interrupt Enable): The master on/off switch for all interrupts.  

‣ Address 0004h (Hexadecimal): The fixed memory address where the CPU (Central Processing Unit) jumps whenever an interrupt occurs to run your ISR (Interrupt Service Routine).  

‣ RETFIE (Return from Interrupt Enable): The instruction at the end of your ISR (Interrupt Service Routine) that restores the saved position from the stack and jumps back to the main program.  

### How It Works:


1. An event happens (e.g., Timer0 overflows and sets T0IF (Timer 0 Interrupt Flag) to 1).  

2. The CPU  saves its current place by pushing the PC (Program Counter) onto the stack and jumps to address 0004h ( Hexadecimal).  

3. The ISR (Interrupt Service Routine) handles the event and clears T0IF (Timer 0 Interrupt Flag).  

4. The RETFIE (Return from Interrupt Enable) instruction sends the CPU  right back to where it was originally working.

   ### Real life example:
You are cooking dinner. The doorbell rings (an interrupt). You put a bookmark on your recipe (save address to stack), walk straight to the front door (jump to address 0004h), answer the door (run the ISR), and return to your recipe bookmark (resume seamlessly) without restarting dinner.
---

## Week 3 Updates & Testing

### What was done:
- Added a working "Toggle RA0" button to test Port A pin states directly from the GUI.
- Added live status displays for Port A and Timer0 in the Hardware panel.
- Connected Timer0 to increment automatically on each CPU instruction step.

### Test Results (Expected vs Actual):

1. **GUI Launch**
   - **Test:** Started the simulator using `java -cp bin SimulatorFrame`.
   - **Expected:** Window opens showing Program Memory, Console, and Hardware registers.
   - **Actual:** GUI launched cleanly with all panels visible and functioning.
   - **Status:** PASS

2. **GPIO Pin Toggle**
   - **Test:** Clicked the "Toggle RA0 button" on the UI.
   - **Expected:** PORT A indicator switches between HIGH and LOW.
   - **Actual:** Pin state toggled immediately with each click.
   - **Status:** PASS

3. **Timer0 Counting**
   - **Test:** Stepped through instructions for Task 1 and Task 2.
   - **Expected:** Timer0 counter increments by 1 after each instruction execution.
   - **Actual:** Counter incremented steadily with each step, reaching 0x0B (11) after 11 instruction steps.
   - **Status:** PASS

4. **Timer0 Overflow**
   - **Test:** Let the counter roll past 255 (0xFF).
   - **Expected:** Counter resets to 0 and trips the overflow flag.
   - **Actual:** Counter rolled over to 0x00 and OVF updated to YES.
   - **Status:** PASS

5. **Full System Integration**
   - **Test:** Ran the cooperative scheduler with both tasks executing.
   - **Expected:** CPU, Memory, Timer0, and GPIO run together without breaking context switches.
   - **Actual:** Tasks switched normally in the console while hardware status updated live.
   - **Status:** PASS
