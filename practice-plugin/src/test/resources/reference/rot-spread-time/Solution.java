package com.jinloes.practice;

public class Solution {
    public static int minutesToRot(int[][] grid) {
        int rows = grid.length;
        if (rows == 0) {
            return 0;
        }
        int cols = grid[0].length;
        boolean[] rotten = new boolean[rows * cols];
        int[] queue = new int[rows * cols];
        int head = 0;
        int tail = 0;
        int fresh = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == 2) {
                    rotten[r * cols + c] = true;
                    queue[tail++] = r * cols + c;
                } else if (grid[r][c] == 1) {
                    fresh++;
                }
            }
        }
        int[] dr = {1, -1, 0, 0};
        int[] dc = {0, 0, 1, -1};
        int minutes = 0;
        while (head < tail && fresh > 0) {
            int levelEnd = tail;
            while (head < levelEnd) {
                int cell = queue[head++];
                for (int d = 0; d < 4; d++) {
                    int r = cell / cols + dr[d];
                    int c = cell % cols + dc[d];
                    if (r < 0 || c < 0 || r >= rows || c >= cols || rotten[r * cols + c] || grid[r][c] != 1) {
                        continue;
                    }
                    rotten[r * cols + c] = true;
                    fresh--;
                    queue[tail++] = r * cols + c;
                }
            }
            minutes++;
        }
        return fresh == 0 ? minutes : -1;
    }
}
