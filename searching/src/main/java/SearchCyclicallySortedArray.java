import java.util.List;

/**
 * Design an O(logn) algorithm for finding the position of the smallest element in a cyclically sorted array.
 * Assume all elements are distinct.
 */
public class SearchCyclicallySortedArray {
    public static int search(List<Integer> toSearch) {
        int start = 0;
        int end = toSearch.size() - 1;

        while (start < end) {
            int mid = start + ((end - start) / 2);

            if (toSearch.get(mid) > toSearch.get(end)) {
                start = mid + 1;
            } else {
                end = mid;
            }
        }

        return start;
    }
}
