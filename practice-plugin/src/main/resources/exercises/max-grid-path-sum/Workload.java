package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {60, 120, 240, 480, 960};
    private Workload() {}
    static Object prepare(int size) {
        int[][] grid = new int[size][size];
        for (int r = 0; r < size; r++) for (int c = 0; c < size; c++) grid[r][c] = (r + c) % 17 - 4;
        return grid;
    }
    static void run(Object state) { Solution.maxPathSum((int[][]) state); }
    static int units(int size) { return 1; }
}
