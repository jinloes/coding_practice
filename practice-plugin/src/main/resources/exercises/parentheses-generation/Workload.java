package com.jinloes.practice;

/**
 * Scaling workload for Parentheses Generation. The result size is exponential in the number of
 * pairs, so the probe fixes six pairs (132 strings) and doubles the number of calls: the measured
 * cost is the cost of one fixed-size generation, and the general bound is explained in the hints.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {256, 512, 1024, 2048, 4096};
    private static final int PAIRS = 6;
    private static final int EXPECTED = 132;

    private Workload() {}

    static Object prepare(int size) {
        return size;
    }

    static void run(Object state) {
        int calls = (Integer) state;
        for (int i = 0; i < calls; i++) {
            if (Solution.generate(PAIRS).size() != EXPECTED) {
                throw new IllegalStateException("Unexpected number of strings");
            }
        }
    }

    static int units(int size) {
        return size;
    }
}
