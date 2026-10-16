package com.jinloes.practice;

/** Scaling workload for Largest Island Area: one solid island in a 64-column grid of "size" cells. */
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
        return grid;
    }

    static void run(Object state) {
        int[][] grid = (int[][]) state;
        if (Solution.largestIsland(grid) != grid.length * COLS) {
            throw new IllegalStateException("Unexpected island area");
        }
    }

    static int units(int size) {
        return 1;
    }
}
