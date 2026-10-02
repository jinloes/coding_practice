package com.jinloes.practice;

import java.util.List;

final class Workload {
    static final String UNIT = "call";
    static final int ROWS = 8;
    static final int[] SIZES = {1024, 2048, 4096, 8192, 16384};

    private Workload() {}

    static Object prepare(int size) {
        int[][] heights = new int[ROWS][size];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < size; c++) {
                heights[r][c] = r + c;
            }
        }
        return heights;
    }

    static void run(Object state) {
        int[][] heights = (int[][]) state;
        List<int[]> cells = Solution.drainageCells(heights);
        int expected = ROWS + heights[0].length - 1;
        if (cells.size() != expected) {
            throw new IllegalStateException("Expected " + expected + " cells but found " + cells.size());
        }
    }

    static int units(int size) { return 1; }
}
