public class CPUProgramCounterTest {

    public static void main(String[] args) {

        ProgramCounter pc = new ProgramCounter();

        System.out.println("Initial PC: " + pc.getPC());

        // Test 1: Initial PC
        if (pc.getPC() == 0) {
            System.out.println("TC01 - Initial PC: PASS");
        } else {
            System.out.println("TC01 - Initial PC: FAIL");
        }

        // Test 2: Increment
        pc.increment();

        if (pc.getPC() == 1) {
            System.out.println("TC02 - PC Increment: PASS");
        } else {
            System.out.println("TC02 - PC Increment: FAIL");
        }

        // Test 3: Jump
        pc.jump(5);

        if (pc.getPC() == 5) {
            System.out.println("TC03 - PC Jump: PASS");
        } else {
            System.out.println("TC03 - PC Jump: FAIL");
        }

        // Test 4: Reset
        pc.reset();

        if (pc.getPC() == 0) {
            System.out.println("TC04 - PC Reset: PASS");
        } else {
            System.out.println("TC04 - PC Reset: FAIL");
        }
    }
}
