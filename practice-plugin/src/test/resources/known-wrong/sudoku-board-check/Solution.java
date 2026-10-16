package com.jinloes.practice;

public class Solution {
    public static boolean isValid(char[][] board) {
        int[] rows = new int[9];
        int[] cols = new int[9];
        int[] boxes = new int[9];
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                char cell = board[r][c];
                if (cell == '.') {
                    continue;
                }
                int bit = 1 << (cell - '1');
                int box = (r / 3) * 3 + c / 3;
                if (((rows[r] | cols[c] ) & bit) != 0) {
                    return false;
                }
                rows[r] |= bit;
                cols[c] |= bit;
            }
        }
        return true;
    }
}
