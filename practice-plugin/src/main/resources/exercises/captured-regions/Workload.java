package com.jinloes.practice;

/** Scaling workload for Captured Regions: a board of isolated O pockets inside an O border, of "size" cells, 64 columns wide. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private static final int COLS = 64;

    private Workload() {}

    static Object prepare(int size) {
        char[][] board = new char[size / COLS][COLS];
        int rows = board.length;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < COLS; c++) {
                boolean border = r == 0 || c == 0 || r == rows - 1 || c == COLS - 1;
                board[r][c] = border || (r % 2 == 0 && c % 2 == 0) ? 'O' : 'X';
            }
        }
        return board;
    }

    static void run(Object state) {
        char[][] board = (char[][]) state;
        Solution.flipEnclosed(board);
        if (board[0][0] != 'O' || board[2][2] != 'X') {
            throw new IllegalStateException("Unexpected board after flipping");
        }
    }

    static int units(int size) {
        return 1;
    }
}
