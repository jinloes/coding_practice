package com.jinloes.practice;

/**
 * Scaling workload for Rising Water Path: a grid of 64 columns and {@code size / 64} rows whose
 * elevations increase in row-major order, so the best route must reach the largest cell.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private static final int COLS = 64;

    private Workload() {}

    static Object prepare(int size) {
        int rows = size / COLS;
        int[][] grid = new int[rows][COLS];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < COLS; c++) {
                grid[r][c] = r * COLS + c;
            }
        }
        return grid;
    }

    static void run(Object state) {
        int[][] grid = (int[][]) state;
        int expected = grid.length * COLS - 1;
        if (Solution.swimTime(grid) != expected) {
            throw new IllegalStateException("Unexpected swim time");
        }
    }

    static int units(int size) {
        return 1;
    }
}
