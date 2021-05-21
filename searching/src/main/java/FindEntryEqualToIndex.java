import java.util.List;

/**
 * Design an efficient algorithm that takes a sorted array of distinct integers, and returns an index i such
 * that the element at index i equals i.
 */
public class FindEntryEqualToIndex {
    public static int find(List<Integer> toSearch) {
        int start = 0;
        int end = toSearch.size() - 1;

        while (start <= end) {
            int mid = start + ((end - start) / 2);
            int val = toSearch.get(mid);

            if (val == mid) {
                return mid;
            } else if (val > mid) {
                end = mid - 1;
            } else {
                start = mid + 1;
            }
        }

        return -1;

    }
}
