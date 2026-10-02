package com.jinloes.practice;

import java.util.Arrays;

/**
 * Scaling workload for Reachable End. Input construction happens in prepare, outside the timed region.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {250000, 500000, 1000000, 2000000, 4000000};

    private Workload() {}

    static Object prepare(int size) {
        int[] values = new int[size];
        Arrays.fill(values, 1);
        return values;
    }

    static void run(Object state) {
        int[] values = (int[]) state;
        boolean result = Solution.canReach(values);
        if (result != true) {
            throw new IllegalStateException("Unexpected result " + result);
        }
    }

    static int units(int size) {
        return 1;
    }
}
