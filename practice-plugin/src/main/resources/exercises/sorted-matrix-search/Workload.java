package com.jinloes.practice;

/** Scaling workload for Sorted Matrix Search; n is the number of cells, laid out 64 per row. */
final class Workload {
    static final String UNIT = "query";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private static final int QUERIES = 4096;

    private Workload() {}

    static Object prepare(int size) {
        int columns = 64;
        int[][] grid = new int[size / columns][columns];
        for (int i = 0; i < size; i++) {
            grid[i / columns][i % columns] = 2 * i;
        }
        return grid;
    }

    /** Even targets are present, odd targets are not. */
    static void run(Object state) {
        int[][] grid = (int[][]) state;
        int cells = grid.length * grid[0].length;
        for (int q = 0; q < QUERIES; q++) {
            int target = (int) ((q * 7919L) % (2L * cells + 2));
            if (Solution.containsValue(grid, target) != (target % 2 == 0 && target < 2 * cells)) {
                throw new IllegalStateException("Wrong answer for " + target);
            }
        }
    }

    static int units(int size) {
        return QUERIES;
    }
}
