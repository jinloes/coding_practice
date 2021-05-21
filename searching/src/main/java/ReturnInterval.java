import java.util.List;

/**
 * Write a program which takes a sorted array A of integers, and an integer k, and returns the interval enclosing
 * k, i.e., the pair of integers L and U such that L is the first occurrence of k in A and U is the last occurrence of
 * k in A.
 */
public class ReturnInterval {
    public static List<Integer> get(List<Integer> list, int k) {
        int start = findStart(list, k);
        int end = findEnd(list, k);

        if (start < 0 || end < 0) {
            start = -1;
            end = -1;
        }

        return List.of(start, end);
    }

    private static int findStart(List<Integer> list, int k) {
        int start = 0;
        int end = list.size() - 1;

        while (start <= end) {
            int mid = start + ((end - start) / 2);
            int currentVal = list.get(mid);
            if (currentVal == k && ((mid > 0 && list.get(mid - 1) != k) || mid == 0)) {
                return mid;
            } else if (currentVal < k) {
                start = mid + 1;
            } else {
                end = mid - 1;
            }
        }

        return -1;
    }

    private static int findEnd(List<Integer> list, int k) {
        int start = 0;
        int end = list.size() - 1;

        while (start <= end) {
            int mid = start + ((end - start) / 2);
            int currentVal = list.get(mid);
            if (currentVal == k && ((mid < list.size() - 1 && list.get(mid + 1) != k) || mid == list.size() - 1)) {
                return mid;
            } else if (currentVal > k) {
                end = mid - 1;
            } else {
                start = mid + 1;
            }
        }

        return -1;
    }
}
