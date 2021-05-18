/**
 * Write a program which takes an array of n integers, where A[i] denotes the maximum you can advance from index i,
 * and returns whether it is possible to advance to the last index starting from the beginning of the array.
 */
public class AdvanceThroughArray {
    public static boolean canAdvance(int[] arr) {
        if (arr == null || arr.length == 0) {
            return true;
        }

        int maxIdx = 0;
        int end = arr.length - 1;

        // O(n)
        for (int i = 0; i <= maxIdx && maxIdx < end; i++) {
            maxIdx = Math.max(maxIdx, i + arr[i]);
        }

        return maxIdx >= end;
    }
}
