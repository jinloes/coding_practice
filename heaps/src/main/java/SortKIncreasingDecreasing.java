import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Design an efficient algorithm for sorting a fc-increasing-decreasing array.
 */
public class SortKIncreasingDecreasing {
    public static List<Integer> sort(List<Integer> toSort) {
        int k = 0;

        int previous = 0;
        for (int i = 0; i < toSort.size(); i++) {
            int current = toSort.get(i);

            if (current < previous) {
                k = i;
                break;
            }

            previous = current;
        }

        List<List<Integer>> sortedSubarrays = new LinkedList<>();

        boolean increasing = true;

        int i = 0;
        while (i < toSort.size()) {
            int end = increasing ? i + k : i + k - 1;
            end = end > toSort.size() ? toSort.size() : end;

            List<Integer> subArray = toSort.subList(i, end);

            if (!increasing) {
                Collections.reverse(subArray);
            }

            increasing = !increasing;
            i = end;

            sortedSubarrays.add(subArray);
        }

        return MergeSortedLists.merge(sortedSubarrays);
    }
}
