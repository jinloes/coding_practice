package com.jinloes.practice;

public class Solution {
    public static int largestIsland(int[][] grid) {
        int rows = grid.length;
        if (rows == 0) {
            return 0;
        }
        int cols = grid[0].length;
        boolean[] seen = new boolean[rows * cols];
        int[] stack = new int[rows * cols];
        int best = 0;
        for (int start = 0; start < rows * cols; start++) {
            if (seen[start] || grid[start / cols][start % cols] != 1) {
                continue;
            }
            int area = 0;
            int top = 0;
            stack[top++] = start;
            seen[start] = true;
            while (top > 0) {
                int cell = stack[--top];
                area++;
                int r = cell / cols;
                int c = cell % cols;
                if (r > 0 && !seen[cell - cols] && grid[r - 1][c] == 1) {
                    seen[cell - cols] = true;
                    stack[top++] = cell - cols;
                }
                if (r + 1 < rows && !seen[cell + cols] && grid[r + 1][c] == 1) {
                    seen[cell + cols] = true;
                    stack[top++] = cell + cols;
                }
                if (c > 0 && !seen[cell - 1] && grid[r][c - 1] == 1) {
                    seen[cell - 1] = true;
                    stack[top++] = cell - 1;
                }
                if (c + 1 < cols && !seen[cell + 1] && grid[r][c + 1] == 1) {
                    seen[cell + 1] = true;
                    stack[top++] = cell + 1;
                }
            }
            best = Math.max(best, area);
        }
        return best;
    }
}
