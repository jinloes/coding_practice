package com.jinloes.practice;

public class Solution {
    public static int longestSubarray(int[] numbers, int limit) {
        int best = 0, current = 0;
        for (int i = 0; i < numbers.length; i++) {
            if (i == 0 || Math.abs(numbers[i] - numbers[i - 1]) <= limit) current++;
            else current = 1;
            best = Math.max(best, current);
        }
        return best;
    }
}
