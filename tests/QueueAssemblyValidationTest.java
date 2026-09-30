
public class QueueAssemblyValidationTest {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println(" PIC16F72 WEEK 3 QUEUE VALIDATION");
        System.out.println("========================================");

        // Create the same components used by the simulator
        ProgramMemory programMemory = new ProgramMemory();
        DataMemory dataMemory = new DataMemory();
        FIFOQueue queue = new FIFOQueue(8);

        // ------------------------------------------------
        // PIC16F72 Assembly-equivalent program
        //
        // MOVLW 10
        // MOVWF 0x30
        //
        // MOVLW 20
        // MOVWF 0x31
        //
        // MOVLW 30
        // MOVWF 0x32
        //
        // SLEEP
        // ------------------------------------------------

        programMemory.addInstruction(new Instruction("MOVLW", 10));
        programMemory.addInstruction(new Instruction("MOVWF", 0x30));

        programMemory.addInstruction(new Instruction("MOVLW", 20));
        programMemory.addInstruction(new Instruction("MOVWF", 0x31));

        programMemory.addInstruction(new Instruction("MOVLW", 30));
        programMemory.addInstruction(new Instruction("MOVWF", 0x32));

        programMemory.addInstruction(new Instruction("SLEEP", 0));

        CPU cpu = new CPU(programMemory, dataMemory);

        System.out.println();
        System.out.println("ASSEMBLY PROGRAM:");
        System.out.println("MOVLW 10");
        System.out.println("MOVWF 0x30");
        System.out.println("MOVLW 20");
        System.out.println("MOVWF 0x31");
        System.out.println("MOVLW 30");
        System.out.println("MOVWF 0x32");
        System.out.println("SLEEP");

        System.out.println();
        System.out.println("----- CPU EXECUTION -----");

        // Execute Assembly-equivalent program
        while (!cpu.isHalted()) {

            Instruction instruction = cpu.fetch();

            if (instruction == null) {
                break;
            }

            String opcode = cpu.decode();

            System.out.println(
                    "FETCH -> DECODE -> EXECUTE : "
                    + opcode
                    + " "
                    + instruction.getOperand()
            );

            cpu.execute();
        }

        // ------------------------------------------------
        // Check memory values
        // ------------------------------------------------

        System.out.println();
        System.out.println("----- MEMORY VALIDATION -----");

        System.out.println(
                "Memory[0x30] = "
                + dataMemory.read(0x30)
        );

        System.out.println(
                "Memory[0x31] = "
                + dataMemory.read(0x31)
        );

        System.out.println(
                "Memory[0x32] = "
                + dataMemory.read(0x32)
        );

        // ------------------------------------------------
        // FIFO ENQUEUE VALIDATION
        // ------------------------------------------------

        System.out.println();
        System.out.println("----- FIFO ENQUEUE -----");

        boolean e1 = queue.enqueue(dataMemory.read(0x30));
        boolean e2 = queue.enqueue(dataMemory.read(0x31));
        boolean e3 = queue.enqueue(dataMemory.read(0x32));

        System.out.println("Enqueue 10: " + (e1 ? "PASS" : "FAIL"));
        System.out.println("Enqueue 20: " + (e2 ? "PASS" : "FAIL"));
        System.out.println("Enqueue 30: " + (e3 ? "PASS" : "FAIL"));

        System.out.println(
                "Queue: " + queue
        );

        // ------------------------------------------------
        // FIFO DEQUEUE VALIDATION
        // ------------------------------------------------

        System.out.println();
        System.out.println("----- FIFO DEQUEUE -----");

        int first = queue.dequeue();
        int second = queue.dequeue();
        int third = queue.dequeue();

        System.out.println("Dequeue 1: " + first);
        System.out.println("Dequeue 2: " + second);
        System.out.println("Dequeue 3: " + third);

        // ------------------------------------------------
        // FIFO CHECK
        // ------------------------------------------------

        boolean fifoCorrect =
                first == 10
                && second == 20
                && third == 30;

        System.out.println();

        if (fifoCorrect) {
            System.out.println("PASS: FIFO ordering is correct.");
        } else {
            System.out.println("FAIL: FIFO ordering is incorrect.");
        }

        // ------------------------------------------------
        // FINAL RESULT
        // ------------------------------------------------

        System.out.println();
        System.out.println("========================================");

        if (fifoCorrect) {
            System.out.println(" ALL QUEUE VALIDATION TESTS PASSED");
        } else {
            System.out.println(" QUEUE VALIDATION FAILED");
        }

        System.out.println("========================================");
    }
}

