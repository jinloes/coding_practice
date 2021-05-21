import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Write a program which takes as input a very long sequence of numbers and prints the numbers in sorted order.
 * Each number is at most k away from its correctly sorted position.
 */
public class SortNearlySortedList {
    public static List<Integer> sort(List<Integer> toSort, int k) {
        PriorityQueue<Integer> queue = new PriorityQueue<>();

        for (int i = 0; i < k + 1; i++) {
            queue.add(toSort.get(i));
        }

        List<Integer> result = new LinkedList<>();

        int idx = k + 1;

        while (!queue.isEmpty()) {
            result.add(queue.poll());
            if (idx < toSort.size()) {
                queue.add(toSort.get(idx));
            }
            idx++;
        }

        return result;
    }
}
