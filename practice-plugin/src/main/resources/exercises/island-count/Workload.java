package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int ROWS = 8;
    static final int[] SIZES = {1024, 2048, 4096, 8192, 16384};

    private Workload() {}

    /** The grid is rebuilt from a template for every repetition because the solution may mark cells as visited. */
    static Object prepare(int size) {
        char[][] template = new char[ROWS][size];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < size; c++) {
                template[r][c] = r % 2 == 0 || c % 4 == 0 ? '1' : '0';
            }
        }
        return template;
    }

    static void run(Object state) {
        char[][] template = (char[][]) state;
        char[][] grid = new char[template.length][];
        for (int r = 0; r < template.length; r++) {
            grid[r] = template[r].clone();
        }
        int islands = Solution.countIslands(grid);
        if (islands != 1) {
            throw new IllegalStateException("Expected one island but found " + islands);
        }
    }

    static int units(int size) { return 1; }
}
