package com.jinloes.practice;

public class Solution {
    public static void flipEnclosed(char[][] board) {
        int rows = board.length;
        if (rows == 0) {
            return;
        }
        int cols = board[0].length;
        int[] stack = new int[rows * cols];
        int top = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                boolean border = r == 0 || c == 0 || r == rows - 1 || c == cols - 1;
                if (border && board[r][c] == 'O') {
                    board[r][c] = '#';
                    stack[top++] = r * cols + c;
                }
            }
        }
        while (top > 0) {
            int cell = stack[--top];
            int r = cell / cols;
            int c = cell % cols;
            if (r > 0 && board[r - 1][c] == 'O') {
                board[r - 1][c] = '#';
                stack[top++] = cell - cols;
            }
            if (r + 1 < rows && board[r + 1][c] == 'O') {
                board[r + 1][c] = '#';
                stack[top++] = cell + cols;
            }
            if (c > 0 && board[r][c - 1] == 'O') {
                board[r][c - 1] = '#';
                stack[top++] = cell - 1;
            }
            if (c + 1 < cols && board[r][c + 1] == 'O') {
                board[r][c + 1] = '#';
                stack[top++] = cell + 1;
            }
        }
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                board[r][c] = board[r][c] == '#' ? 'O' : 'X';
            }
        }
    }
}
