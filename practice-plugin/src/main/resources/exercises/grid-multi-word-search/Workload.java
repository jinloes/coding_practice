package com.jinloes.practice;

import java.util.HashSet;
import java.util.List;

/**
 * Scaling workload for Grid Multi-Word Search. The word list is fixed (four words of at most five letters) and
 * the grid grows to n cells by keeping 64 rows and adding columns, so each cell starts a bounded search and the
 * total time is O(n) in the number of cells. The grid is restored by the search, so it is reused across runs.
 */
final class Workload {
    static final String UNIT = "grid cell";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private static final int ROWS = 64;
    private static final String[] WORDS = {"abcde", "edcba", "aaaa", "bcdea"};

    private Workload() {}

    static Object prepare(int size) {
        int columns = size / ROWS;
        char[][] grid = new char[ROWS][columns];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < columns; c++) {
                grid[r][c] = (char) ('a' + (r + c) % 5);
            }
        }
        return grid;
    }

    static void run(Object state) {
        List<String> found = Solution.findWords((char[][]) state, WORDS.clone());
        if (!new HashSet<>(found).equals(new HashSet<>(List.of("abcde", "edcba", "bcdea")))) {
            throw new IllegalStateException("Unexpected words " + found);
        }
    }

    static int units(int size) {
        return 1;
    }
}
