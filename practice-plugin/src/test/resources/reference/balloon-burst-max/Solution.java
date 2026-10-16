package com.jinloes.practice;

public class Solution {
    public static int maxCoins(int[] values) {
        int n = values.length;
        int[] padded = new int[n + 2];
        padded[0] = 1;
        padded[n + 1] = 1;
        System.arraycopy(values, 0, padded, 1, n);
        int[][] best = new int[n + 2][n + 2];
        for (int length = 1; length <= n; length++) {
            for (int left = 1; left + length - 1 <= n; left++) {
                int right = left + length - 1;
                int top = 0;
                for (int last = left; last <= right; last++) {
                    int coins = best[left][last - 1] + padded[left - 1] * padded[last] * padded[right + 1]
                            + best[last + 1][right];
                    top = Math.max(top, coins);
                }
                best[left][right] = top;
            }
        }
        return n == 0 ? 0 : best[1][n];
    }
}
