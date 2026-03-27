import java.util.Deque;
import java.util.LinkedList;

/**
 * Implement a queue with enqueue, dequeue, and max operations. The max operation returns the maximum element
 * currently stored in the queue.
 */
public class MaxQueue {
    private final Deque<Integer> maxQueue;
    private final Deque<Integer> queue;

    public MaxQueue() {
        this.maxQueue = new LinkedList<>();
        this.queue = new LinkedList<>();
    }

    public void enqueue(int val) {
        while (!maxQueue.isEmpty() && maxQueue.peek() < val) {
            maxQueue.poll();
        }
        maxQueue.add(val);
        queue.add(val);
    }

    public int dequeue() {
        int val = queue.pollFirst();
        if (maxQueue.peek() == val) {
            maxQueue.poll();
        }

        return val;
    }

    public int max() {
        return maxQueue.peek();
    }
}
