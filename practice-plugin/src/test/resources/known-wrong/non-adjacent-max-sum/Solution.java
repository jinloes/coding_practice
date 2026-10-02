package com.jinloes.practice;

public class Solution {
    public static long maxSum(int[] values) {
        int skipped = 0;
        int taken = 0;
        for (int value : values) {
            int best = Math.max(taken, skipped + value);
            skipped = taken;
            taken = best;
        }
        return taken;
    }
}
