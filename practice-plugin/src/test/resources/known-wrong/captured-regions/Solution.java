package com.jinloes.practice;

public class Solution {
    public static void flipEnclosed(char[][] board) {
        int rows = board.length;
        if (rows == 0) {
            return;
        }
        int cols = board[0].length;
        for (int c = 0; c < cols; c++) {
            keep(board, 0, c);
            keep(board, rows - 1, c);
        }
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                board[r][c] = board[r][c] == '#' ? 'O' : 'X';
            }
        }
    }

    private static void keep(char[][] board, int r, int c) {
        if (r < 0 || c < 0 || r >= board.length || c >= board[0].length || board[r][c] != 'O') {
            return;
        }
        board[r][c] = '#';
        keep(board, r + 1, c);
        keep(board, r - 1, c);
        keep(board, r, c + 1);
        keep(board, r, c - 1);
    }
}
