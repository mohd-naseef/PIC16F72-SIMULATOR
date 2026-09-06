public class Main {

    public static void main(String[] args) {

        // Create memories
        ProgramMemory programMemory = new ProgramMemory();
        DataMemory dataMemory = new DataMemory();

        // Load test program - all 8 selected instructions
        programMemory.addInstruction(new Instruction("MOVLW", 10));
        programMemory.addInstruction(new Instruction("MOVWF", 20));
        programMemory.addInstruction(new Instruction("SUBLW", 15));
        programMemory.addInstruction(new Instruction("ANDLW", 3));
        programMemory.addInstruction(new Instruction("MOVLW", 5));
        programMemory.addInstruction(new Instruction("ADDLW", 3));
        programMemory.addInstruction(new Instruction("INCF", 20));
        programMemory.addInstruction(new Instruction("GOTO", 9));
        programMemory.addInstruction(new Instruction("MOVLW", 99)); // skipped by GOTO
        programMemory.addInstruction(new Instruction("SLEEP", 0));

        // Create CPU
        CPU cpu = new CPU(programMemory, dataMemory);

        System.out.println("===== PIC16F72 CPU SIMULATOR =====");
        System.out.println();

        // Fetch-Decode-Execute cycle
        while (!cpu.isHalted()) {

            // FETCH
            Instruction instruction = cpu.fetch();

            if (instruction == null) {
                break;
            }

            // DECODE
            String opcode = cpu.decode();

            System.out.println(
                "PC: " + (cpu.getPC() - 1)
                + " | Instruction: " + opcode
                + " | Operand: " + instruction.getOperand()
            );

            // EXECUTE
            cpu.execute();

            System.out.println(
                "W = " + cpu.getW()
                + " | STATUS = " + cpu.getSTATUS()
            );

            System.out.println();
        }

        // Final CPU state
        System.out.println("===== PROGRAM FINISHED =====");
        System.out.println("W Register: " + cpu.getW());
        System.out.println("Program Counter: " + cpu.getPC());
        System.out.println("STATUS Register: " + cpu.getSTATUS());
        System.out.println("Memory[20]: " + dataMemory.read(20));
    }
}

           
