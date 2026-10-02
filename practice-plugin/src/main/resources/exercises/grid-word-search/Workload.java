package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {1 << 16, 1 << 17, 1 << 18, 1 << 19, 1 << 20, 1 << 21};

    private Workload() {
    }

    private static final int COLUMNS = 64;

    /**
     * Search time is bounded by cells * branching^wordLength. The word length is held at a fixed 5 and
     * the grid grows in rows, so measured growth is linear in the number of cells (the size).
     */
    static Object prepare(int size) {
        int rows = size / COLUMNS;
        char[][] grid = new char[rows][COLUMNS];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                grid[r][c] = (char) ('A' + (r + c) % 5);
            }
        }
        return grid;
    }

    /** The word contains a letter missing from the grid, so every start is explored and none succeeds. */
    static void run(Object state) {
        if (Solution.wordExists((char[][]) state, "ABCDZ")) {
            throw new IllegalStateException("The probe word cannot exist in this grid");
        }
    }

    static int units(int size) {
        return 1;
    }
}
