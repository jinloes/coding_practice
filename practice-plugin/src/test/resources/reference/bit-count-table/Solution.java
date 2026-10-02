package com.jinloes.practice;

public class Solution {
    public static int[] countBits(int n) {
        int[] counts = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            counts[i] = counts[i >> 1] + (i & 1);
        }
        return counts;
    }
}
