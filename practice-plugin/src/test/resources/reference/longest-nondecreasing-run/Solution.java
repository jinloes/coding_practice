package com.jinloes.practice;

public class Solution {
    public static int longestRun(int[] numbers) {
        if (numbers.length == 0) return 0;
        int best = 1;
        int current = 1;
        for (int i = 1; i < numbers.length; i++) {
            current = numbers[i] >= numbers[i - 1] ? current + 1 : 1;
            best = Math.max(best, current);
        }
        return best;
    }
}
