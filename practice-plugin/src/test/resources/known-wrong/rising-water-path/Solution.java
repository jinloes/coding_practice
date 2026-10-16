package com.jinloes.practice;

public class Solution {
    public static int swimTime(int[][] elevation) {
        int rows = elevation.length;
        int cols = elevation[0].length;
        int[][] best = new int[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (r == 0 && c == 0) {
                    best[r][c] = elevation[0][0];
                    continue;
                }
                int fromAbove = r > 0 ? best[r - 1][c] : Integer.MAX_VALUE;
                int fromLeft = c > 0 ? best[r][c - 1] : Integer.MAX_VALUE;
                best[r][c] = Math.max(elevation[r][c], Math.min(fromAbove, fromLeft));
            }
        }
        return best[rows - 1][cols - 1];
    }
}
