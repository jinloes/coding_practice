package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public static List<List<String>> solveQueens(int n) {
        if (n < 1) {
            throw new IllegalArgumentException("n must be at least 1");
        }
        List<List<String>> boards = new ArrayList<>();
        place(0, n, new int[n], new boolean[n], new boolean[2 * n], new boolean[2 * n], boards);
        return boards;
    }

    private static void place(int row, int n, int[] queenColumn, boolean[] columns, boolean[] down, boolean[] up,
                              List<List<String>> boards) {
        if (row == n) {
            List<String> board = new ArrayList<>(n);
            for (int r = 0; r < n; r++) {
                char[] cells = new char[n];
                java.util.Arrays.fill(cells, '.');
                cells[queenColumn[r]] = 'Q';
                board.add(new String(cells));
            }
            boards.add(board);
            return;
        }
        for (int c = 0; c < n; c++) {
            if (columns[c] || down[row - c + n]) {
                continue;
            }
            columns[c] = down[row - c + n] = up[row + c] = true;
            queenColumn[row] = c;
            place(row + 1, n, queenColumn, columns, down, up, boards);
            columns[c] = down[row - c + n] = up[row + c] = false;
        }
    }
}
