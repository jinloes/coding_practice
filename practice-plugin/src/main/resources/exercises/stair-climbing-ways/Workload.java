package com.jinloes.practice;

/**
 * Scaling workload for Stair Climbing Ways. One call is bounded by 90 stairs, so the size axis is
 * the number of maximum-size calls in a batch; the reported cost per batch grows linearly with it.
 */
final class Workload {
    static final String UNIT = "batch of calls";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        return size;
    }

    static void run(Object state) {
        int calls = (Integer) state;
        long total = 0;
        for (int i = 0; i < calls; i++) {
            total += Solution.countWays(90);
        }
        if (total != 4660046610375530309L * calls) {
            throw new IllegalStateException("Unexpected way count");
        }
    }

    static int units(int size) {
        return 1;
    }
}
