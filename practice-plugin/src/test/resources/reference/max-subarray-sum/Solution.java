package com.jinloes.practice;

public class Solution {
    public static long maxSubarraySum(int[] numbers) {
        long endingHere = numbers[0];
        long best = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            endingHere = Math.max(numbers[i], endingHere + numbers[i]);
            best = Math.max(best, endingHere);
        }
        return best;
    }
}
