package com.jinloes.practice;

public class Solution {
    public static int maxPathSum(int[][] grid) {
        int sum = 0;
        for (int[] row : grid) for (int value : row) if (value > 0) sum += value;
        return sum;
    }
}
