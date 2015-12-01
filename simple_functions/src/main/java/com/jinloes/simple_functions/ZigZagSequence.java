package com.jinloes.simple_functions;

/**
 * Finds the largest zigzag sequence in an array.
 */
public class ZigZagSequence {
    public static int findLongest(int[] arr) {
        if (arr.length == 0) {
            return 0;
        }
        if (arr.length == 1) {
            return 1;
        }
        if (arr.length == 2) {
            return 2;
        }
        int[] diff = new int[arr.length - 1];
        for (int i = 1; i < arr.length; i++) {
            diff[i - 1] = arr[i] - arr[i - 1];
        }
        int previousSign = diff[0];
        int count = 1;
        for (int sign : diff) {
            if (previousSign * sign < 0) {
                previousSign = sign;
                count++;
            }
        }
        return count + 1;
    }
}
