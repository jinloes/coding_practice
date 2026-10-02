package com.jinloes.practice;

import java.util.Arrays;

public class Solution {
    private static final int MOD = 1_000_000_007;

    public static int count(int rows, int cols) {
        int[] ways = new int[cols];
        Arrays.fill(ways, 1);
        for (int row = 1; row < rows; row++) {
            for (int col = 1; col < cols; col++) {
                ways[col] = (ways[col] + ways[col - 1]) % MOD;
            }
        }
        return ways[cols - 1];
    }
}
