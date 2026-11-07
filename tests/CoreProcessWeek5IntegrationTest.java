public class CoreProcessWeek5IntegrationTest {
    public static void main(String[] args) {
        CoreProcess core = new CoreProcess();
        String loaded = core.loadProgram();
        if (!loaded.startsWith("RESULT: PROGRAM_LOADED")) {
            throw new AssertionError("Program failed to load: " + loaded);
        }
        String listing = core.getProgramListing();
        if (!listing.contains("HENC") || !listing.contains("FLIP") || !listing.contains("HDEC")) {
            throw new AssertionError("Hamming instructions missing from Core program listing: " + listing);
        }
        String edit = core.editInstruction(13, "FLIP", 0x40, 2);
        if (!edit.startsWith("RESULT: INSTRUCTION_UPDATED")) {
            throw new AssertionError("Program Memory edit failed: " + edit);
        }
        String run = core.runProgram();
        if (!run.startsWith("RESULT: RUN_COMPLETE")) {
            throw new AssertionError("Core run failed: " + run);
        }
        if (core.readMemory(0x20) != 0x86) {
            throw new AssertionError("Core/Scheduler run did not recover original byte.");
        }
        int stored = core.readMemory(0x40) | ((core.readMemory(0x41) & 0x0F) << 8);
        if (Hamming128.syndrome(stored) != 0) {
            throw new AssertionError("Core/Scheduler did not store corrected codeword.");
        }
        System.out.println("PASS: Core loaded Hamming demo and Scheduler executed it.");
        System.out.println("PASS: Recovered RAM[0x20] = 0x86; corrected codeword = 0x"
                + Integer.toHexString(stored).toUpperCase());
    }
}
