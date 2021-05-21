import java.util.List;

/**
 * Write a method that takes a sorted array and a key and returns the index of the first occurrence of that key
 * in the array.
 */
public class SearchSortedArray {
    public static int search(List<Integer> toSearch, int val) {
        int start = 0;
        int end = toSearch.size() - 1;

        while (start < end) {
            int mid = start + ((end - start) / 2);
            int currentVal = toSearch.get(mid);
            if (currentVal == val) {
                return mid;
            } else if (currentVal > val) {
                end = mid - 1;
            } else {
                start = mid + 1;
            }
        }

        return -1;
    }
}
