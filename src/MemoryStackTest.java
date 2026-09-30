public class MemoryStackTest {

    public static void main(String[] args) {

        DataMemory memory = new DataMemory();

        // Test 1: RAM Write and Read
        memory.write(10, 55);

        if (memory.read(10) == 55) {
            System.out.println("RAM READ/WRITE: PASS");
        } else {
            System.out.println("RAM READ/WRITE: FAIL");
        }


        // Test 2: PUSH and POP
        memory.push(25);

        if (memory.pop() == 25) {
            System.out.println("PUSH/POP: PASS");
        } else {
            System.out.println("PUSH/POP: FAIL");
        }


        // Test 3: Multiple PUSH/POP - LIFO
        memory.push(10);
        memory.push(20);
        memory.push(30);

        if (memory.pop() == 30 &&
            memory.pop() == 20 &&
            memory.pop() == 10) {

            System.out.println("MULTIPLE PUSH/POP: PASS");
        } else {
            System.out.println("MULTIPLE PUSH/POP: FAIL");
        }


        // Test 4: Stack Pointer
        memory.push(100);
        memory.push(200);

        if (memory.getSP() == 2) {
            System.out.println("STACK POINTER: PASS");
        } else {
            System.out.println("STACK POINTER: FAIL");
        }


        // Test 5: RESET
        memory.write(20, 99);
        memory.push(50);

        memory.reset();

        if (memory.read(20) == 0 &&
            memory.getSP() == 0) {

            System.out.println("RESET: PASS");
        } else {
            System.out.println("RESET: FAIL");
        }
    }
}
