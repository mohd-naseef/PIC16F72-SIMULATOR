public class InstructionTest {

    public static void main(String[] args) {

        ProgramMemory progMem = new ProgramMemory();
        DataMemory dataMem = new DataMemory();
        CPU cpu = new CPU(progMem, dataMem);

        System.out.println("--- INSTRUCTION TEST ---");

        // Test 1: MOVLW
        progMem.addInstruction(new Instruction("MOVLW", 25));

        cpu.fetch();
        cpu.decode();
        cpu.execute();
        cpu.update_Status();

        if (cpu.getW() == 25) {
            System.out.println("TC01 - MOVLW: PASS");
        } else {
            System.out.println("TC01 - MOVLW: FAIL");
        }


        // Test 2: MOVWF
        progMem.addInstruction(new Instruction("MOVWF", 20));

        cpu.fetch();
        cpu.decode();
        cpu.execute();
        cpu.update_Status();

        if (dataMem.read(20) == 25) {
            System.out.println("TC02 - MOVWF: PASS");
        } else {
            System.out.println("TC02 - MOVWF: FAIL");
        }


        // Test 3: ADDLW
        progMem.addInstruction(new Instruction("ADDLW", 5));

        cpu.fetch();
        cpu.decode();
        cpu.execute();
        cpu.update_Status();

        if (cpu.getW() == 30) {
            System.out.println("TC03 - ADDLW: PASS");
        } else {
            System.out.println("TC03 - ADDLW: FAIL");
        }


        // Test 4: SUBLW
        progMem.addInstruction(new Instruction("SUBLW", 40));

        cpu.fetch();
        cpu.decode();
        cpu.execute();
        cpu.update_Status();

        if (cpu.getW() == 10) {
            System.out.println("TC04 - SUBLW: PASS");
        } else {
            System.out.println("TC04 - SUBLW: FAIL");
        }


        // Test 5: ANDLW
        progMem.addInstruction(new Instruction("ANDLW", 3));

        cpu.fetch();
        cpu.decode();
        cpu.execute();
        cpu.update_Status();

        if (cpu.getW() == 2) {
            System.out.println("TC05 - ANDLW: PASS");
        } else {
            System.out.println("TC05 - ANDLW: FAIL");
        }


        // Test 6: INCF
        dataMem.write(20, 10);

        progMem.addInstruction(new Instruction("INCF", 20));

        cpu.fetch();
        cpu.decode();
        cpu.execute();
        cpu.update_Status();

        if (dataMem.read(20) == 11) {
            System.out.println("TC06 - INCF: PASS");
        } else {
            System.out.println("TC06 - INCF: FAIL");
        }


        // Test 7: GOTO
        progMem.addInstruction(new Instruction("GOTO", 9));

        cpu.fetch();
        cpu.decode();
        cpu.execute();
        cpu.update_Status();

        if (cpu.getPC() == 9) {
            System.out.println("TC07 - GOTO: PASS");
        } else {
            System.out.println("TC07 - GOTO: FAIL");
        }


        // Test 8: SLEEP
        progMem.addInstruction(new Instruction("SLEEP", 0));

        cpu.fetch();
        cpu.decode();
        cpu.execute();
        cpu.update_Status();

        if (cpu.isHalted()) {
            System.out.println("TC08 - SLEEP: PASS");
        } else {
            System.out.println("TC08 - SLEEP: FAIL");
        }


        System.out.println("--- ALL INSTRUCTION TESTS COMPLETE ---");
    }
}