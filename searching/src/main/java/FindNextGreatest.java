import java.util.List;

/**
 * Design an efficient algorithm that takes a sorted array and a key, and finds the index of the first occurrence of
 * an element greater than that key.
 */
public class FindNextGreatest {
    public static int search(List<Integer> toSearch, int val) {
        int start = 0;
        int end = toSearch.size() - 1;
        int mid = start + ((end - start) / 2);
        int searchEnd = toSearch.size() - 1;

        while (start < end) {
            mid = start + ((end - start) / 2);
            int currentVal = toSearch.get(mid);
            if (currentVal == val && (mid != searchEnd && toSearch.get(mid + 1) != val)) {
                return mid == toSearch.size() - 1 ? mid : mid + 1;
            } else if (currentVal > val) {
                end = mid - 1;
            } else {
                start = mid + 1;
            }
        }

        if (mid > 0 && (toSearch.get(mid) > val && toSearch.get(mid - 1) > val)) {
            return mid - 1;
        } else if (toSearch.get(mid) > val) {
            return mid;
        } else {
            return mid == toSearch.size() - 1 ? mid : mid + 1;
        }
    }
}
