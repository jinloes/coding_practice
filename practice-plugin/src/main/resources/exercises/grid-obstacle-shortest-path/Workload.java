package com.jinloes.practice;

final class Workload {
    static final String UNIT = "cell";
    static final int[] SIZES = {32, 48, 64, 80, 96};
    private Workload() {}
    static Object prepare(int size) {
        int[][] grid = new int[size][size];
        for (int r = 1; r < size; r += 3) for (int c = 0; c < size - 1; c++) grid[r][c] = 1;
        return grid;
    }
    static void run(Object state) { int[][] grid = (int[][]) state; int result = Solution.shortestPath(grid, grid.length); if (result <= 0) throw new IllegalStateException("bad path"); }
    static int units(int size) { return size * size; }
}
