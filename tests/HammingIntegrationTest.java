public class HammingIntegrationTest {
    public static void main(String[] args) {
        int cases = 0;
        for (int value = 0; value < 256; value++) {
            int encoded = Hamming128.encode(value);
            if (Hamming128.decode(encoded) != value) {
                throw new AssertionError("Clean decode failed for " + value);
            }
            for (int bit = 0; bit < 12; bit++) {
                int damaged = Hamming128.flip(encoded, bit);
                int decoded = Hamming128.decode(damaged);
                if (decoded != value) {
                    throw new AssertionError(
                            "Single-bit correction failed: data=" + value + ", bit=" + bit);
                }
                cases++;
            }
        }

        ProgramMemory program = new ProgramMemory();
        DataMemory memory = new DataMemory();
        program.addInstruction(new Instruction("MOVLW", 0x86));
        program.addInstruction(new Instruction("MOVWF", 0x20));
        program.addInstruction(new Instruction("HENC", 0x20, 0x40));
        program.addInstruction(new Instruction("FLIP", 0x40, 2));
        program.addInstruction(new Instruction("HDEC", 0x20, 0x40));
        program.addInstruction(new Instruction("SLEEP", 0));
        CPU cpu = new CPU(program, memory);

        while (!cpu.isHalted()) {
            Instruction instruction = cpu.fetch();
            if (instruction == null) break;
            cpu.decode();
            cpu.execute();
        }

        if (memory.read(0x20) != 0x86) {
            throw new AssertionError("Integrated Hamming demo did not recover 0x86");
        }
        int correctedCodeword = memory.read(0x40) | ((memory.read(0x41) & 0x0F) << 8);
        if (Hamming128.syndrome(correctedCodeword) != 0) {
            throw new AssertionError("Stored codeword was not corrected");
        }

        System.out.println("PASS: " + cases + " single-bit correction cases.");
        System.out.println("PASS: CPU HENC/FLIP/HDEC demo recovered RAM[0x20] = 0x86.");
        System.out.printf("PASS: Corrected 12-bit codeword = 0x%03X%n", correctedCodeword);
    }
}
