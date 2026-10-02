package com.jinloes.practice;

public class Solution {
    public static long maxSubarraySum(int[] numbers) {
        long endingHere = 0;
        long best = 0;
        for (int value : numbers) {
            endingHere = Math.max(0, endingHere + value);
            best = Math.max(best, endingHere);
        }
        return best;
    }
}
