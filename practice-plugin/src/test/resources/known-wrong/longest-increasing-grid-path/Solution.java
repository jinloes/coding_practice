package com.jinloes.practice;

public class Solution {
    public static int longestRise(int[][] grid) {
        int rows = grid.length;
        int cols = grid[0].length;
        int[][] best = new int[rows][cols];
        int answer = 1;
        for (int r = rows - 1; r >= 0; r--) {
            for (int c = cols - 1; c >= 0; c--) {
                best[r][c] = 1;
                if (r + 1 < rows && grid[r + 1][c] > grid[r][c]) {
                    best[r][c] = Math.max(best[r][c], 1 + best[r + 1][c]);
                }
                if (c + 1 < cols && grid[r][c + 1] > grid[r][c]) {
                    best[r][c] = Math.max(best[r][c], 1 + best[r][c + 1]);
                }
                answer = Math.max(answer, best[r][c]);
            }
        }
        return answer;
    }
}
