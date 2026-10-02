package com.jinloes.practice;

import java.util.List;

/**
 * Scaling workload for Combination Sum. The candidates are fixed at {1, 2}, which yields about
 * target / 2 combinations of length up to target, so the work and output grow quadratically with
 * the target even though the problem is exponential in general.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {256, 512, 1024, 2048, 4096};
    private static final int[] CANDIDATES = {1, 2};

    private Workload() {}

    static Object prepare(int size) {
        return size;
    }

    static void run(Object state) {
        int target = (Integer) state;
        List<List<Integer>> result = Solution.combinations(CANDIDATES, target);
        if (result.size() != target / 2 + 1) {
            throw new IllegalStateException("Unexpected combination count");
        }
    }

    static int units(int size) {
        return 1;
    }
}
