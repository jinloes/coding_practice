package com.jinloes.practice;

/**
 * Scaling workload for All Permutations. The problem is factorial in general, so each call permutes the
 * same four values (24 orderings) and the size n is the number of calls in one run, which makes the work
 * and allocation grow linearly in n.
 */
final class Workload {
    static final String UNIT = "run";
    static final int[] SIZES = {2048, 4096, 8192, 16384, 32768};

    private Workload() {}

    static Object prepare(int size) {
        return size;
    }

    static void run(Object state) {
        int calls = (Integer) state;
        int[] values = {1, 2, 3, 4};
        int total = 0;
        for (int i = 0; i < calls; i++) {
            total += Solution.permutations(values).size();
        }
        if (total != 24 * calls) {
            throw new IllegalStateException("Unexpected permutation count");
        }
    }

    static int units(int size) {
        return 1;
    }
}
