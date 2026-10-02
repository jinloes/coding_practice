package com.jinloes.practice;

public class Solution {
    public static boolean wordExists(char[][] grid, String word) {
        for (int r = 0; r < grid.length; r++) {
            for (int c = 0; c < grid[r].length; c++) {
                if (search(grid, word, r, c, 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean search(char[][] grid, String word, int r, int c, int index) {
        if (r < 0 || c < 0 || r >= grid.length || c >= grid[r].length || grid[r][c] != word.charAt(index)) {
            return false;
        }
        if (index == word.length() - 1) {
            return true;
        }
        boolean found = search(grid, word, r + 1, c, index + 1)
                || search(grid, word, r - 1, c, index + 1)
                || search(grid, word, r, c + 1, index + 1)
                || search(grid, word, r, c - 1, index + 1);
        return found;
    }
}
