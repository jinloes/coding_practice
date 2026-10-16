package com.jinloes.practice;

/** Scaling workload for Longest Increasing Grid Path; input construction happens outside the timed region. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    private static final int ROWS = 64;

    static Object prepare(int size) {
        int cols = size / ROWS;
        int[][] grid = new int[ROWS][cols];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = r * cols + c;
            }
        }
        return grid;
    }

    /** Values rise in reading order, so the best path walks right and down: rows + cols - 1 cells. */
    static void run(Object state) {
        int[][] grid = (int[][]) state;
        if (Solution.longestRise(grid) != grid.length + grid[0].length - 1) {
            throw new IllegalStateException("Wrong path length");
        }
    }

    static int units(int size) {
        return 1;
    }
}
