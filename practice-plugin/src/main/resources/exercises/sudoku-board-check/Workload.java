package com.jinloes.practice;

/**
 * Scaling workload for Sudoku Board Check. The board is a fixed 9x9 grid, so the size is the number
 * of boards checked in one run and the cost per board is what is measured.
 */
final class Workload {
    static final String UNIT = "board";
    static final int[] SIZES = {1024, 2048, 4096, 8192, 16384};

    private Workload() {}

    static Object prepare(int size) {
        char[][] board = new char[9][9];
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                board[r][c] = (char) ('1' + (3 * (r % 3) + r / 3 + c) % 9);
            }
        }
        return new Object[]{board, size};
    }

    static void run(Object state) {
        Object[] parts = (Object[]) state;
        char[][] board = (char[][]) parts[0];
        int boards = (Integer) parts[1];
        for (int i = 0; i < boards; i++) {
            if (!Solution.isValid(board)) {
                throw new IllegalStateException("Solved board must be valid");
            }
        }
    }

    static int units(int size) {
        return size;
    }
}
