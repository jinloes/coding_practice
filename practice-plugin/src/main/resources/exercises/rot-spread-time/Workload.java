package com.jinloes.practice;

/** Scaling workload for Rot Spread Time: all-fresh 64-column grid with one rotten corner. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private static final int COLS = 64;

    private Workload() {}

    static Object prepare(int size) {
        int[][] grid = new int[size / COLS][COLS];
        for (int[] row : grid) {
            java.util.Arrays.fill(row, 1);
        }
        grid[0][0] = 2;
        return grid;
    }

    static void run(Object state) {
        int[][] grid = (int[][]) state;
        if (Solution.minutesToRot(grid) != grid.length - 1 + COLS - 1) {
            throw new IllegalStateException("Unexpected minutes");
        }
    }

    static int units(int size) {
        return 1;
    }
}
