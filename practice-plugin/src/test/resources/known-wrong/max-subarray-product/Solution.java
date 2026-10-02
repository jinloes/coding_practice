package com.jinloes.practice;

public class Solution {
    public static long maxSubarrayProduct(int[] numbers) {
        long current = numbers[0];
        long best = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            current = Math.max(numbers[i], current * numbers[i]);
            best = Math.max(best, current);
        }
        return best;
    }
}
