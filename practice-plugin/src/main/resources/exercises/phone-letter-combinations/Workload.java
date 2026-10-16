package com.jinloes.practice;

/**
 * Scaling workload for Phone Letter Combinations. The problem is exponential in the number of digits, so
 * each call uses the same three digits (27 strings) and the size n is the number of calls in one run,
 * which makes the work and allocation grow linearly in n.
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
        int total = 0;
        for (int i = 0; i < calls; i++) {
            total += Solution.combinations("234").size();
        }
        if (total != 27 * calls) {
            throw new IllegalStateException("Unexpected combination count");
        }
    }

    static int units(int size) {
        return 1;
    }
}
