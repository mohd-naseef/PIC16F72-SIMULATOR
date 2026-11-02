public class FIFOQueueTest {

    public static void main(String[] args) {

        System.out.println("==============================");
        System.out.println("       FIFO QUEUE TEST");
        System.out.println("==============================");

        testEnqueue();
        testDequeue();
        testFIFO();
        testEmpty();
        testFull();
        testReset();
        testCircular();

        System.out.println("==============================");
        System.out.println("       ALL TESTS PASSED");
        System.out.println("==============================");
    }

    // Test adding values to the queue
    private static void testEnqueue() {

        FIFOQueue queue = new FIFOQueue(5);

        boolean result1 = queue.enqueue(10);
        boolean result2 = queue.enqueue(20);

        if (result1 && result2 && queue.size() == 2) {
            System.out.println("PASS: Enqueue");
        } else {
            throw new AssertionError("Enqueue test failed");
        }
    }

    // Test removing a value from the queue
    private static void testDequeue() {

        FIFOQueue queue = new FIFOQueue(5);

        queue.enqueue(10);
        queue.enqueue(20);

        int value = queue.dequeue();

        if (value == 10) {
            System.out.println("PASS: Dequeue");
        } else {
            throw new AssertionError("Dequeue test failed");
        }
    }

    // Test FIFO ordering
    private static void testFIFO() {

        FIFOQueue queue = new FIFOQueue(5);

        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);

        int first = queue.dequeue();
        int second = queue.dequeue();
        int third = queue.dequeue();

        if (first == 10 && second == 20 && third == 30) {
            System.out.println("PASS: FIFO ordering");
        } else {
            throw new AssertionError("FIFO ordering failed");
        }
    }

    // Test empty queue condition
    private static void testEmpty() {

        FIFOQueue queue = new FIFOQueue(5);

        if (queue.isEmpty()) {
            System.out.println("PASS: Empty queue");
        } else {
            throw new AssertionError("Queue should be empty");
        }
    }

    // Test full queue condition
    private static void testFull() {

        FIFOQueue queue = new FIFOQueue(3);

        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);

        if (!queue.isFull()) {
            throw new AssertionError("Queue should be full");
        }

        // enqueue() returns false when queue is full
        boolean result = queue.enqueue(40);

        if (!result) {
            System.out.println("PASS: Full queue");
        } else {
            throw new AssertionError(
                "Full queue should reject enqueue"
            );
        }
    }

    // Test reset
    private static void testReset() {

        FIFOQueue queue = new FIFOQueue(5);

        queue.enqueue(10);
        queue.enqueue(20);

        queue.reset();

        if (queue.isEmpty() && queue.size() == 0) {
            System.out.println("PASS: Reset");
        } else {
            throw new AssertionError("Reset test failed");
        }
    }

    // Test circular queue behavior
    private static void testCircular() {

        FIFOQueue queue = new FIFOQueue(3);

        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);

        // Remove two values
        queue.dequeue();
        queue.dequeue();

        // Add new values to the freed positions
        boolean result1 = queue.enqueue(40);
        boolean result2 = queue.enqueue(50);

        int first = queue.dequeue();
        int second = queue.dequeue();
        int third = queue.dequeue();

        if (result1 && result2
                && first == 30
                && second == 40
                && third == 50) {

            System.out.println("PASS: Circular queue");

        } else {
            throw new AssertionError(
                "Circular queue test failed"
            );
        }
    }
}
