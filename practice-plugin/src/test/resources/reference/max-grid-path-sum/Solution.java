package com.jinloes.practice;

public class Solution {
    public static int maxPathSum(int[][] grid) {
        if (grid.length == 0 || grid[0].length == 0) return 0;
        int[] dp = new int[grid[0].length];
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                if (row == 0 && col == 0) dp[col] = grid[row][col];
                else if (row == 0) dp[col] = dp[col - 1] + grid[row][col];
                else if (col == 0) dp[col] += grid[row][col];
                else dp[col] = Math.max(dp[col], dp[col - 1]) + grid[row][col];
            }
        }
        return dp[dp.length - 1];
    }
}
