package com.jinloes.binary_chop;

/**
 * Implements a binary search algorithm with integers using recursive and iterative approaches.
 */
public class BinaryChopper {
    public static final int NOT_FOUND = -1;

    public static int chopRecursive(int num, int[] values) {
        if (values == null || values.length == 0) {
            return NOT_FOUND;
        }
        int start = 0;
        int end = values.length - 1;
        return chopInternalRecursive(num, values, start, end);
    }

    private static int chopInternalRecursive(int num, int[] values, int start, int end) {
        if (start > end) {
            return NOT_FOUND;
        } else {
            int midIdx = start + ((end - start) / 2);
            int middleVal = values[midIdx];
            // the value is in the lesser subset
            if (middleVal > num) {
                return chopInternalRecursive(num, values, start, midIdx - 1);
            } else if (middleVal < num) {
                // the value is in the greater subset
                return chopInternalRecursive(num, values, midIdx + 1, end);
            } else {
                // the value was found
                return midIdx;
            }
        }
    }

    public static int chopInterative(int num, int[] values) {
        if (values == null || values.length == 0) {
            return NOT_FOUND;
        }
        int start = 0;
        int end = values.length - 1;
        // while the subset is not empty
        while (end >= start) {
            int mid = start + ((end - start) / 2);
            int midVal = values[mid];
            // if the midpoint is greater search the lesser subset
            if (midVal > num) {
                end = mid - 1;
            } else if (midVal < num) {
                // if the midpoint is less search the greater subset
                start = mid + 1;
            } else {
                // the value has been found
                return mid;
            }
        }
        // the value was not found
        return NOT_FOUND;
    }

}
