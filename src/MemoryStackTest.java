public class MemoryStackTest {

    public static void main(String[] args) {

        DataMemory memory = new DataMemory();

        System.out.println("====================================");
        System.out.println(" PIC16F72 MEMORY + STACK TEST");
        System.out.println("====================================");

        // TC01: Normal RAM READ / WRITE
        memory.write(10, 55);

        if (memory.read(10) == 55) {
            System.out.println("TC01 RAM READ/WRITE: PASS");
        } else {
            System.out.println("TC01 RAM READ/WRITE: FAIL");
        }


        // TC02: RAM ADDRESS 0
        memory.write(0, 100);

        if (memory.read(0) == 100) {
            System.out.println("TC02 RAM ADDRESS 0: PASS");
        } else {
            System.out.println("TC02 RAM ADDRESS 0: FAIL");
        }


        // TC03: RAM ADDRESS 255
        memory.write(255, 200);

        if (memory.read(255) == 200) {
            System.out.println("TC03 RAM ADDRESS 255: PASS");
        } else {
            System.out.println("TC03 RAM ADDRESS 255: FAIL");
        }


        // TC04: RAM 8-BIT MASKING
        memory.write(20, 0x1FF);

        if (memory.read(20) == 0xFF) {
            System.out.println("TC04 RAM 8-BIT MASKING: PASS");
        } else {
            System.out.println("TC04 RAM 8-BIT MASKING: FAIL");
        }


        // TC05: STACK PUSH / POP
        memory.push(25);

        if (memory.pop() == 25) {
            System.out.println("TC05 PUSH/POP: PASS");
        } else {
            System.out.println("TC05 PUSH/POP: FAIL");
        }


        // TC06: STACK LIFO ORDER
        memory.push(10);
        memory.push(20);
        memory.push(30);

        int first = memory.pop();
        int second = memory.pop();
        int third = memory.pop();

        if (first == 30 && second == 20 && third == 10) {
            System.out.println("TC06 STACK LIFO: PASS");
        } else {
            System.out.println("TC06 STACK LIFO: FAIL");
        }


        // TC07: STACK POINTER
        memory.push(100);
        memory.push(200);

        if (memory.getSP() == 2) {
            System.out.println("TC07 STACK POINTER: PASS");
        } else {
            System.out.println("TC07 STACK POINTER: FAIL");
        }

        memory.pop();
        memory.pop();


        // TC08: STACK 13-BIT MASKING
        memory.push(0x3FFF);

        int value = memory.pop();

        if (value == 0x1FFF) {
            System.out.println("TC08 STACK 13-BIT MASKING: PASS");
        } else {
            System.out.println("TC08 STACK 13-BIT MASKING: FAIL");
        }


        // TC09: STACK UNDERFLOW
        try {

            memory.pop();

            System.out.println("TC09 STACK UNDERFLOW: FAIL");

        } catch (IllegalStateException e) {

            System.out.println("TC09 STACK UNDERFLOW: PASS");
        }


        // TC10: STACK OVERFLOW
        try {

            for (int i = 0; i < 8; i++) {
                memory.push(i);
            }

            memory.push(99);

            System.out.println("TC10 STACK OVERFLOW: FAIL");

        } catch (IllegalStateException e) {

            System.out.println("TC10 STACK OVERFLOW: PASS");
        }


        // TC11: RESET
        memory.reset();

        if (memory.read(10) == 0 &&
            memory.read(20) == 0 &&
            memory.getSP() == 0) {

            System.out.println("TC11 RESET: PASS");

        } else {

            System.out.println("TC11 RESET: FAIL");
        }


        System.out.println();
        System.out.println("====================================");
        System.out.println(" MEMORY + STACK TEST COMPLETE");
        System.out.println("====================================");
    }
}
