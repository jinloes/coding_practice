package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

/**
 * Scaling workload for Queens Placement. Placement is exponential in the board size, so the probe
 * solves a fixed 6-by-6 board "size" times and keeps every result; time and retained output then
 * grow linearly with size.
 */
final class Workload {
    static final String UNIT = "run";
    static final int[] SIZES = {16, 32, 64, 128, 256};

    private Workload() {}

    static Object prepare(int size) {
        return size;
    }

    static void run(Object state) {
        int repeats = (Integer) state;
        List<List<List<String>>> results = new ArrayList<>(repeats);
        for (int i = 0; i < repeats; i++) {
            results.add(Solution.solveQueens(6));
        }
        for (List<List<String>> boards : results) {
            if (boards.size() != 4) {
                throw new IllegalStateException("Unexpected board count");
            }
        }
    }

    static int units(int size) {
        return 1;
    }
}
