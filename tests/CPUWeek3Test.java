public class CPUWeek3Test {

    public static void main(String[] args) {

        ProgramMemory progMem = new ProgramMemory();
        DataMemory dataMem = new DataMemory();
        CPU cpu = new CPU(progMem, dataMem);

        System.out.println("--- CPU WEEK 3 TEST ---");

        // Test FETCH
        progMem.addInstruction(new Instruction("MOVLW", 10));

        cpu.fetch();

        if (cpu.getPC() == 1) {
            System.out.println("TC01 - FETCH and PC update: PASS");
        } else {
            System.out.println("TC01 - FETCH and PC update: FAIL");
        }

        // Test DECODE
        String opcode = cpu.decode();

        if ("MOVLW".equals(opcode)) {
            System.out.println("TC02 - DECODE: PASS");
        } else {
            System.out.println("TC02 - DECODE: FAIL");
        }

        // Test EXECUTE
        cpu.execute();
        cpu.update_Status();

        if (cpu.getW() == 10) {
            System.out.println("TC03 - EXECUTE MOVLW: PASS");
        } else {
            System.out.println("TC03 - EXECUTE MOVLW: FAIL");
        }

        // Test PC continues correctly
        progMem.addInstruction(new Instruction("ADDLW", 5));

        cpu.fetch();
        cpu.decode();
        cpu.execute();
        cpu.update_Status();

        if (cpu.getPC() == 2 && cpu.getW() == 15) {
            System.out.println("TC04 - PC and W update: PASS");
        } else {
            System.out.println("TC04 - PC and W update: FAIL");
        }

        System.out.println("--- CPU WEEK 3 TEST COMPLETE ---");
    }
}