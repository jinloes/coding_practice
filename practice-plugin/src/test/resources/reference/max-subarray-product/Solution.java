package com.jinloes.practice;

public class Solution {
    public static long maxSubarrayProduct(int[] numbers) {
        long max = numbers[0];
        long min = numbers[0];
        long best = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            long value = numbers[i];
            long high = max * value;
            long low = min * value;
            max = Math.max(value, Math.max(high, low));
            min = Math.min(value, Math.min(high, low));
            best = Math.max(best, max);
        }
        return best;
    }
}
