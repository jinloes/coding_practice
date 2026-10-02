package com.jinloes.practice;

public class Solution {
    public static int countIslands(char[][] grid) {
        int count = 0;
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                boolean land = grid[r][c] == '1';
                boolean landAbove = r > 0 && grid[r - 1][c] == '1';
                boolean landLeft = c > 0 && grid[r][c - 1] == '1';
                if (land && !landAbove && !landLeft) {
                    count++;
                }
            }
        }
        return count;
    }
}
