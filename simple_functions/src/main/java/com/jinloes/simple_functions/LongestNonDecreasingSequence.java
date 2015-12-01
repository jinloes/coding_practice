package com.jinloes.simple_functions;

import java.util.HashMap;
import java.util.Map;

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
        Map<Integer, Integer> length = new HashMap<>();
        int runningLargest = 0;
        for (int val : arr) {
            int largestLength = 0;
            // Search for the entry that's less than the value and has the longest increasing
            // sequence
            for (Map.Entry<Integer, Integer> entry : length.entrySet()) {
                if (entry.getKey() < val && entry.getValue() > largestLength) {
                    largestLength = entry.getValue();
                }
            }
            largestLength++;
            if (largestLength > runningLargest) {
                runningLargest = largestLength;
            }
            length.put(val, largestLength);
        }
        return runningLargest;
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
        int longestNonDecreasingSequence = 0;
        int currentLongestNonDecreasingSequence = 0;
        int previous = arr[0];
        for (int anArr : arr) {
            if (anArr >= previous) {
                currentLongestNonDecreasingSequence++;
            } else {
                if (currentLongestNonDecreasingSequence > longestNonDecreasingSequence) {
                    longestNonDecreasingSequence = currentLongestNonDecreasingSequence;
                }
                currentLongestNonDecreasingSequence = 1;
            }
            previous = anArr;
        }
        if (currentLongestNonDecreasingSequence > longestNonDecreasingSequence) {
            longestNonDecreasingSequence = currentLongestNonDecreasingSequence;
        }
        return longestNonDecreasingSequence;
    }
}
