import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InstructionSetTest {

    private ProgramMemory programMemory;
    private DataMemory dataMemory;
    private CPU cpu;

    // STATUS flag bit definitions from CPU.java
    private static final int C_FLAG = 0;
    private static final int DC_FLAG = 1;
    private static final int Z_FLAG = 2;

    @BeforeEach
    public void setUp() {
        programMemory = new ProgramMemory();
        dataMemory = new DataMemory();
        cpu = new CPU(programMemory, dataMemory);
    }

    private void runSingleInstruction(Instruction instr) {
        programMemory.clear();
        programMemory.addInstruction(instr);
        cpu.setPC(0);
        cpu.fetch();
        cpu.execute();
    }

    private boolean isFlagSet(int bit) {
        return (cpu.getSTATUS() & (1 << bit)) != 0;
    }

    // --- 1. MOVLW ---
    @Test
    @DisplayName("MOVLW: Loads 8-bit literal into W register")
    public void testMOVLW() {
        runSingleInstruction(new Instruction("MOVLW", 0x42));
        assertEquals(0x42, cpu.getW());
    }

    // --- 2. MOVWF ---
    @Test
    @DisplayName("MOVWF: Stores W register into DataMemory address")
    public void testMOVWF() {
        cpu.setW(0x7A);
        runSingleInstruction(new Instruction("MOVWF", 0x15));
        assertEquals(0x7A, dataMemory.read(0x15));
    }

    // --- 3. ADDLW ---
    @Test
    @DisplayName("ADDLW: Addition and verifies Carry, Digit Carry, and Zero Flags")
    public void testADDLW() {
        // Normal add
        cpu.setW(10);
        runSingleInstruction(new Instruction("ADDLW", 5));
        assertEquals(15, cpu.getW());
        assertFalse(isFlagSet(Z_FLAG));
        assertFalse(isFlagSet(C_FLAG));

        // Overflow & Carry check
        cpu.reset();
        cpu.setW(250);
        runSingleInstruction(new Instruction("ADDLW", 10));
        assertEquals(4, cpu.getW());
        assertTrue(isFlagSet(C_FLAG));

        // Zero flag & Digit Carry check
        cpu.reset();
        cpu.setW(255);
        runSingleInstruction(new Instruction("ADDLW", 1));
        assertEquals(0, cpu.getW());
        assertTrue(isFlagSet(Z_FLAG));
        assertTrue(isFlagSet(C_FLAG));
        assertTrue(isFlagSet(DC_FLAG));
    }

    // --- 4. SUBLW ---
    @Test
    @DisplayName("SUBLW: Subtracts W from literal (k - W) and verifies PIC borrow/carry logic")
    public void testSUBLW() {
        // No borrow (operand >= W) -> C = 1
        cpu.setW(5);
        runSingleInstruction(new Instruction("SUBLW", 15));
        assertEquals(10, cpu.getW());
        assertTrue(isFlagSet(C_FLAG));
        assertFalse(isFlagSet(Z_FLAG));

        // Zero result
        cpu.reset();
        cpu.setW(12);
        runSingleInstruction(new Instruction("SUBLW", 12));
        assertEquals(0, cpu.getW());
        assertTrue(isFlagSet(Z_FLAG));
        assertTrue(isFlagSet(C_FLAG));

        // Borrow occurred (operand < W) -> C = 0
        cpu.reset();
        cpu.setW(20);
        runSingleInstruction(new Instruction("SUBLW", 10));
        assertEquals(0xF6, cpu.getW());
        assertFalse(isFlagSet(C_FLAG));
        assertFalse(isFlagSet(Z_FLAG));
    }

    // --- 5. ANDLW ---
    @Test
    @DisplayName("ANDLW: Performs bitwise AND with literal")
    public void testANDLW() {
        cpu.setW(0b11001100);
        runSingleInstruction(new Instruction("ANDLW", 0b10101010));
        assertEquals(0b10001000, cpu.getW());
        assertFalse(isFlagSet(Z_FLAG));

        cpu.setW(0x0F);
        runSingleInstruction(new Instruction("ANDLW", 0xF0));
        assertEquals(0, cpu.getW());
        assertTrue(isFlagSet(Z_FLAG));
    }

    // --- 6. INCF ---
    @Test
    @DisplayName("INCF: Increments DataMemory value and checks wrap-around to 0")
    public void testINCF() {
        dataMemory.write(0x30, 20);
        runSingleInstruction(new Instruction("INCF", 0x30));
        assertEquals(21, dataMemory.read(0x30));
        assertFalse(isFlagSet(Z_FLAG));

        dataMemory.write(0x30, 255);
        runSingleInstruction(new Instruction("INCF", 0x30));
        assertEquals(0, dataMemory.read(0x30));
        assertTrue(isFlagSet(Z_FLAG));
    }

    // --- 7. GOTO ---
    @Test
    @DisplayName("GOTO: Direct jump of PC to operand target")
    public void testGOTO() {
        runSingleInstruction(new Instruction("GOTO", 14));
        assertEquals(14, cpu.getPC());
    }

    // --- 8. SLEEP ---
    @Test
    @DisplayName("SLEEP: Sets halted state on the CPU")
    public void testSLEEP() {
        assertFalse(cpu.isHalted());
        runSingleInstruction(new Instruction("SLEEP", 0));
        assertTrue(cpu.isHalted());
    }
}