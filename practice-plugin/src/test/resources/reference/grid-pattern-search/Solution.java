package com.jinloes.practice;

public class Solution {
    public static boolean containsPattern(String[] grid, String[] pattern) {
        if (grid.length == 0 || pattern.length == 0 || pattern[0].isEmpty()) return false;
        int rows = grid.length, cols = grid[0].length();
        int pr = pattern.length, pc = pattern[0].length();
        if (pr > rows || pc > cols) return false;
        for (int row = 0; row <= rows - pr; row++) {
            for (int col = 0; col <= cols - pc; col++) {
                if (matches(grid, pattern, row, col, pr, pc)) return true;
            }
        }
        return false;
    }
    private static boolean matches(String[] grid, String[] pattern, int row, int col, int pr, int pc) {
        for (int r = 0; r < pr; r++) for (int c = 0; c < pc; c++) {
            if (grid[row + r].charAt(col + c) != pattern[r].charAt(c)) return false;
        }
        return true;
    }
}
