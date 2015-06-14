package com.jinloes.simple_functions;

/**
 * Finds the length of the longest non-decreasing sequence
 */
public class LongestNonDecreasingSequence {
    public static int findLongestNonDecreasingSequence(int arr[]) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        int longestNonDecreasingSequence = 0;
        int currentLongestNonDecreasingSequence = 0;
        int previous = arr[0];
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] >= previous) {
                currentLongestNonDecreasingSequence++;
            } else {
                if (currentLongestNonDecreasingSequence > longestNonDecreasingSequence) {
                    longestNonDecreasingSequence = currentLongestNonDecreasingSequence;
                }
                currentLongestNonDecreasingSequence = 1;
            }
            previous = arr[i];
        }
        if (currentLongestNonDecreasingSequence > longestNonDecreasingSequence) {
            longestNonDecreasingSequence = currentLongestNonDecreasingSequence;
        }
        return longestNonDecreasingSequence;
    }
}
