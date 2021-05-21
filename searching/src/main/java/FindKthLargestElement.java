import java.util.List;
import java.util.PriorityQueue;

/**
 * Design an algorithm for computing the kth largest element in an array. Assume entries are distinct.
 */
public class FindKthLargestElement {
    public static int find(List<Integer> toSearch, int k) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>();

        for (Integer val : toSearch) {
            maxHeap.add(val);

            if (maxHeap.size() > k) {
                maxHeap.poll();
            }
        }

        return maxHeap.poll();
    }
}
