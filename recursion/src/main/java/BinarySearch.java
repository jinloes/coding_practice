/**
 * Implement a function to perform a binary search on a sorted array of
 * integers to find the index of a given integer.
 */
public class BinarySearch {
    public static int search(int[] arr, int toSearch) {
        return doSearch(arr, toSearch, 0, arr.length - 1);
    }

    private static int doSearch(int[] arr, int toSearch, int start, int end) {
        if (start > end) {
            return -1;
        }
        int mid = start + ((end - start) / 2);

        if (arr[mid] > toSearch) {
            return doSearch(arr, toSearch, start, mid - 1);
        } else if (arr[mid] < toSearch) {
            return doSearch(arr, toSearch, mid + 1, end);
        } else {
            return mid;
        }
    }
}
