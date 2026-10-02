package com.jinloes.practice;

public class Solution {
    public static long maxSum(int[] values) {
        int n = values.length;
        if (n == 0) {
            return 0;
        }
        if (n == 1) {
            return values[0];
        }
        return Math.max(linear(values, 0, n - 2), linear(values, 1, n - 1));
    }

    private static long linear(int[] values, int from, int to) {
        long skipped = 0;
        long taken = 0;
        for (int i = from; i <= to; i++) {
            long best = Math.max(taken, skipped + values[i]);
            skipped = taken;
            taken = best;
        }
        return taken;
    }
}
