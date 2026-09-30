public class FIFOQueueTest {

    public static void main(String[] args) {

        System.out.println(
            "=============================="
        );

        System.out.println(
            "       FIFO QUEUE TEST"
        );

        System.out.println(
            "=============================="
        );

        testEnqueue();

        testFIFO();

        testEmpty();

        testFull();

        testClear();

        testCircularBehaviour();

        System.out.println(
            "=============================="
        );

        System.out.println(
            "   ALL QUEUE TESTS PASSED"
        );

        System.out.println(
            "=============================="
        );
    }

    private static void testEnqueue() {

        FIFOQueue queue =
            new FIFOQueue(8);

        queue.enqueue(10);

        if (queue.size() != 1) {
            throw new AssertionError(
                "Enqueue failed"
            );
        }

        System.out.println(
            "PASS: Enqueue"
        );
    }

    private static void testFIFO() {

        FIFOQueue queue =
            new FIFOQueue(8);

        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);

        int first = queue.dequeue();
        int second = queue.dequeue();
        int third = queue.dequeue();

        if (first != 10 ||
            second != 20 ||
            third != 30) {

            throw new AssertionError(
                "FIFO ordering failed"
            );
        }

        System.out.println(
            "PASS: FIFO ordering"
        );
    }

    private static void testEmpty() {

        FIFOQueue queue =
            new FIFOQueue(8);

        if (!queue.isEmpty()) {

            throw new AssertionError(
                "New queue should be empty"
            );
        }

        try {

            queue.dequeue();

            throw new AssertionError(
                "Empty dequeue should fail"
            );

        } catch (IllegalStateException e) {

            System.out.println(
                "PASS: Empty queue"
            );
        }
    }

    private static void testFull() {

        FIFOQueue queue =
            new FIFOQueue(3);

        queue.enqueue(10);
        queue.enqueue(20);
        queue.enqueue(30);

        if (!queue.isFull()) {

            throw new AssertionError(
                "Queue should be full"
            );
        }

        try {

            queue.enqueue(40);

            throw new AssertionError(
                "Full queue should reject enqueue"
            );

        } catch (IllegalStateException e) {

            System.out.println(
                "PASS: Full queue"
            );
        }
    }

    private static void testClear() {

        FIFOQueue queue =
            new FIFOQueue(8);

        queue.enqueue(10);
        queue.enqueue(20);

        queue.clear();

        if (!queue.isEmpty()) {

            throw new AssertionError(
                "Clear failed"
            );
        }

        System.out.println(
            "PASS: Clear"
        );
    }

    private static void testCircularBehaviour() {

        FIFOQueue queue =
            new FIFOQueue(3);

        queue.enqueue(10);
        queue.enqueue(20);

        // Remove 10
        int removed = queue.dequeue();

        if (removed != 10) {

            throw new AssertionError(
                "Wrong item removed"
            );
        }

        // This should reuse the freed space
        queue.enqueue(30);

        int first = queue.dequeue();
        int second = queue.dequeue();

        if (first != 20 || second != 30) {

            throw new AssertionError(
                "Circular FIFO behaviour failed"
            );
        }

        System.out.println(
            "PASS: Circular queue behaviour"
        );
    }
}