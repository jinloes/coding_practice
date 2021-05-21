import java.util.*;

/**
 * Design an algorithm for computing the running median of a sequence.
 */
public class ComputeMedian {
    public static List<Double> onlineMedian(Iterator<Integer> sequence) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.<Integer>naturalOrder().reversed());
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();

        List<Double> medians = new LinkedList<>();

        while (sequence.hasNext()) {
            int next = sequence.next();

            if (minHeap.isEmpty()) {
                minHeap.add(next);
            } else if (next >= minHeap.peek()) {
                minHeap.add(next);
            } else {
                maxHeap.add(next);
            }

            if (minHeap.size() > maxHeap.size() + 1) {
                maxHeap.add(minHeap.poll());
            } else if (maxHeap.size() > minHeap.size()) {
                minHeap.add(maxHeap.poll());
            }

            if (minHeap.size() == maxHeap.size()) {
                double median = (minHeap.peek() + maxHeap.peek()) / 2.0;
                medians.add(median);
            } else {
                medians.add((double) minHeap.peek());
            }
        }

        return medians;
    }
}
