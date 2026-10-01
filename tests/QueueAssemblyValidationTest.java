import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class QueueAssemblyValidationTest {

    public static void main(String[] args) {

        System.out.println("========================================");
        System.out.println(" PIC16F72 WEEK 3 QUEUE VALIDATION");
        System.out.println("========================================");

        // Create the same components used by the simulator
        ProgramMemory programMemory = new ProgramMemory();
        DataMemory dataMemory = new DataMemory();
        FIFOQueue queue = new FIFOQueue(8);

        // Load the actual Assembly file
        String fileName = "tests\\QueueAssemblyValidation.asm";

        try {
            loadAssemblyFile(fileName, programMemory);
        } catch (IOException e) {
            System.out.println("ERROR: Could not read Assembly file.");
            System.out.println(e.getMessage());
            return;
        }

        System.out.println();
        System.out.println("Assembly file loaded successfully:");
        System.out.println(fileName);

        System.out.println();
        System.out.println("----- CPU EXECUTION -----");

        // Create CPU using the loaded Assembly program
        CPU cpu = new CPU(programMemory, dataMemory);

        // Execute the loaded Assembly program
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

        int value1 = dataMemory.read(0x30);
        int value2 = dataMemory.read(0x31);
        int value3 = dataMemory.read(0x32);

        System.out.println("Memory[0x30] = " + value1);
        System.out.println("Memory[0x31] = " + value2);
        System.out.println("Memory[0x32] = " + value3);

        // Check that Assembly execution stored the
        // expected values in memory.
        if (value1 == 10 && value2 == 20 && value3 == 30) {
            System.out.println("PASS: Assembly memory values");
        } else {
            System.out.println("FAIL: Assembly memory values");
        }

        // ------------------------------------------------
        // FIFO ENQUEUE VALIDATION
        // ------------------------------------------------

        System.out.println();
        System.out.println("----- FIFO ENQUEUE -----");

        boolean e1 = queue.enqueue(value1);
        boolean e2 = queue.enqueue(value2);
        boolean e3 = queue.enqueue(value3);

        System.out.println(
                "Enqueue " + value1 + ": "
                + (e1 ? "PASS" : "FAIL")
        );

        System.out.println(
                "Enqueue " + value2 + ": "
                + (e2 ? "PASS" : "FAIL")
        );

        System.out.println(
                "Enqueue " + value3 + ": "
                + (e3 ? "PASS" : "FAIL")
        );

        System.out.println("Queue: " + queue);

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

        if (value1 == 10
                && value2 == 20
                && value3 == 30
                && e1
                && e2
                && e3
                && fifoCorrect) {

            System.out.println(" ALL QUEUE VALIDATION TESTS PASSED");

        } else {

            System.out.println(" QUEUE VALIDATION FAILED");
        }

        System.out.println("========================================");
    }


    // ====================================================
    // LOAD ASSEMBLY FILE
    // ====================================================

    private static void loadAssemblyFile(
            String fileName,
            ProgramMemory programMemory
    ) throws IOException {

        BufferedReader reader =
                new BufferedReader(
                        new FileReader(fileName)
                );

        String line;

        while ((line = reader.readLine()) != null) {

            // Remove comments
            int commentPosition = line.indexOf(';');

            if (commentPosition >= 0) {
                line = line.substring(0, commentPosition);
            }

            line = line.trim();

            // Ignore empty lines
            if (line.isEmpty()) {
                continue;
            }

            // Separate opcode and operand
            String[] parts =
                    line.split("\\s+");

            String opcode =
                    parts[0].toUpperCase();

            int operand = 0;

            // Read operand if present
            if (parts.length > 1) {
                operand = parseOperand(parts[1]);
            }

            // Add instruction to ProgramMemory
            programMemory.addInstruction(
                    new Instruction(opcode, operand)
            );
        }

        reader.close();
    }


    // ====================================================
    // CONVERT ASSEMBLY OPERAND
    // ====================================================

    private static int parseOperand(String text) {

        text = text.trim();

        // Hexadecimal value such as 0x30
        if (text.startsWith("0x")
                || text.startsWith("0X")) {

            return Integer.parseInt(
                    text.substring(2),
                    16
            );
        }

        // Decimal value such as 10, 20, 30
        return Integer.parseInt(text);
    }
}


