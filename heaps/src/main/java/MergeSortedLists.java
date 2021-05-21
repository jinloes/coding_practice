import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Write a program that takes as input a set of sorted sequences and computes the union of these sequences as a
 * sorted sequence.
 */
public class MergeSortedLists {
    public static List<Integer> merge(List<List<Integer>> lists) {
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        List<Integer> result = new LinkedList<>();

        for (List<Integer> list : lists) {
            queue.add(list.get(0));
        }

        int i = 1;


        while (!queue.isEmpty()) {
            for (List<Integer> list : lists) {
                if (i < list.size()) {
                    queue.add(list.get(i));
                }
            }
            result.add(queue.poll());
            i++;
        }

        return result;
    }
}
