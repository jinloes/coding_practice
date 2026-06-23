package com.jinloes.arrays;

import java.util.Arrays;

/**
 * Calulcates longest increasing sequences in different ways.
 */
public class LongestNonDecreasingSequence {
    /**
     * Finds the longest increasing sequence in an array (non consecutive).
     *
     * @param arr array to check
     * @return the length of the longest increasing sequence
     */
    public static int findLongestIncreasingSequence(int[] arr) {
        if (arr == null || arr.length == 0) {
            return 0;
        }

        int[] tails = new int[arr.length];
        int size = 0;

        for (int value : arr) {
            int index = Arrays.binarySearch(tails, 0, size, value);
            if (index < 0) {
                index = -index - 1;
            }
            tails[index] = value;
            if (index == size) {
                size++;
            }
        }

        return size;
    }

    /**
     * Finds the length of the longest non-decreasing sequence of consecutive numbers.
     *
     * @param arr array to check
     * @return length of longest non decreasing sequence
     */
    public static int findLongestNonDecreasingSequence(int[] arr) {
        if (arr == null || arr.length == 0) {
            return 0;
        }

        int longest = 1;
        int current = 1;

        for (int i = 1; i < arr.length; i++) {
            if (arr[i] >= arr[i - 1]) {
                current++;
            } else {
                longest = Math.max(longest, current);
                current = 1;
            }
        }

        return Math.max(longest, current);
    }
}
