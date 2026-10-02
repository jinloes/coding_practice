package com.jinloes.practice;

public class Solution {
    public static long maxSum(int[] values) {
        long skipped = 0;
        long taken = 0;
        for (int value : values) {
            long best = Math.max(taken, skipped + value);
            skipped = taken;
            taken = best;
        }
        return taken;
    }
}
