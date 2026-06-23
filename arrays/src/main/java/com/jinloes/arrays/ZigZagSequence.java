package com.jinloes.arrays;

/**
 * Finds the largest zigzag sequence in an array.
 */
public class ZigZagSequence {
    public static int findLongest(int[] arr) {
        if (arr == null || arr.length == 0) {
            return 0;
        }

        int length = 1;
        int previousDiff = 0;

        for (int i = 1; i < arr.length; i++) {
            int currentDiff = Integer.compare(arr[i], arr[i - 1]);
            if (currentDiff != 0 && currentDiff != previousDiff) {
                length++;
                previousDiff = currentDiff;
            }
        }

        return length;
    }
}
