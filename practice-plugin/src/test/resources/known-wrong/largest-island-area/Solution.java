package com.jinloes.practice;

public class Solution {
    public static int largestIsland(int[][] grid) {
        int rows = grid.length;
        if (rows == 0) {
            return 0;
        }
        int cols = grid[0].length;
        boolean[][] seen = new boolean[rows][cols];
        int best = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                best = Math.max(best, fill(grid, seen, r, c));
            }
        }
        return best;
    }

    private static int fill(int[][] grid, boolean[][] seen, int r, int c) {
        if (r < 0 || c < 0 || r >= grid.length || c >= grid[0].length || seen[r][c] || grid[r][c] != 1) {
            return 0;
        }
        seen[r][c] = true;
        int area = 1;
        for (int dr = -1; dr <= 1; dr++) {
            for (int dc = -1; dc <= 1; dc++) {
                area += fill(grid, seen, r + dr, c + dc);
            }
        }
        return area;
    }
}
