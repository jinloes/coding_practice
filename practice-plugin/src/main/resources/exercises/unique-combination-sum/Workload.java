package com.jinloes.practice;

import java.util.Arrays;

/**
 * Scaling workload for Unique Combination Sum. The candidates are n copies of 1 with a target of n / 2,
 * which has exactly one combination of length n / 2, so the work and output grow linearly in n even though
 * the problem is exponential in general.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {8192, 16384, 32768, 65536, 131072};

    private Workload() {}

    static Object prepare(int size) {
        int[] candidates = new int[size];
        Arrays.fill(candidates, 1);
        return candidates;
    }

    static void run(Object state) {
        int[] candidates = (int[]) state;
        var result = Solution.combinations(candidates, candidates.length / 2);
        if (result.size() != 1 || result.get(0).size() != candidates.length / 2) {
            throw new IllegalStateException("Unexpected combinations");
        }
    }

    static int units(int size) {
        return 1;
    }
}
