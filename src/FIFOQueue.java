public class FIFOQueue {

    private final int[] queue;

    private int front;
    private int rear;
    private int size;

    // Default constructor
    public FIFOQueue() {
        this(8);
    }

    // Constructor with capacity
    public FIFOQueue(int capacity) {

        if (capacity <= 0) {
            throw new IllegalArgumentException(
                    "Queue capacity must be greater than 0"
            );
        }

        queue = new int[capacity];

        front = 0;
        rear = 0;
        size = 0;
    }

    // =====================================================
    // ENQUEUE
    // =====================================================

    public boolean enqueue(int value) {

        if (isFull()) {
            return false;
        }

        queue[rear] = value & 0xFF;

        rear = (rear + 1) % queue.length;

        size++;

        return true;
    }

    // =====================================================
    // DEQUEUE
    // =====================================================

    public int dequeue() {

        if (isEmpty()) {
            throw new IllegalStateException(
                    "Queue is empty"
            );
        }

        int value = queue[front];

        front = (front + 1) % queue.length;

        size--;

        return value;
    }

    // =====================================================
    // PEEK
    // =====================================================

    public int peek() {

        if (isEmpty()) {
            throw new IllegalStateException(
                    "Queue is empty"
            );
        }

        return queue[front];
    }

    // =====================================================
    // CHECK EMPTY
    // =====================================================

    public boolean isEmpty() {
        return size == 0;
    }

    // =====================================================
    // CHECK FULL
    // =====================================================

    public boolean isFull() {
        return size == queue.length;
    }

    // =====================================================
    // SIZE
    // =====================================================

    public int size() {
        return size;
    }

    // =====================================================
    // CAPACITY
    // =====================================================

    public int capacity() {
        return queue.length;
    }

    // =====================================================
    // RESET
    // =====================================================

    public void reset() {

        front = 0;
        rear = 0;
        size = 0;

        for (int i = 0; i < queue.length; i++) {
            queue[i] = 0;
        }
    }

    // =====================================================
    // GET BUFFER
    // =====================================================

    public int[] getBuffer() {

        return queue.clone();
    }

    // =====================================================
    // GET CONTENTS IN FIFO ORDER
    // =====================================================

    public int[] getContents() {

        int[] contents = new int[size];

        for (int i = 0; i < size; i++) {

            contents[i] =
                    queue[(front + i) % queue.length];
        }

        return contents;
    }

    // =====================================================
    // STRING DISPLAY
    // =====================================================

    @Override
    public String toString() {

        if (isEmpty()) {
            return "[EMPTY]";
        }

        StringBuilder result =
                new StringBuilder();

        for (int i = 0; i < size; i++) {

            if (i > 0) {
                result.append(" -> ");
            }

            result.append(
                    String.format(
                            "0x%02X",
                            queue[(front + i) % queue.length]
                    )
            );
        }

        return result.toString();
    }
}


