package com.jinloes.practice;

/**
 * Scaling workload for Min-Cost Stairs. A single call is very short, so each run repeats it a fixed
 * number of times over the same staircase; the repeat count does not depend on the size.
 */
final class Workload {
    static final String UNIT = "run";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private static final int REPEATS = 40;

    private Workload() {}

    static Object prepare(int size) {
        int[] costs = new int[size];
        for (int i = 0; i < size; i++) {
            costs[i] = i % 7 + 1;
        }
        return costs;
    }

    static void run(Object state) {
        int[] costs = (int[]) state;
        for (int i = 0; i < REPEATS; i++) {
            if (Solution.minCostToTop(costs) <= 0) {
                throw new IllegalStateException("Every step has a positive toll, so the cost must be positive");
            }
        }
    }

    static int units(int size) {
        return 1;
    }
}
