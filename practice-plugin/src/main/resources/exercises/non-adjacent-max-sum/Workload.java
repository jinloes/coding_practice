package com.jinloes.practice;

import java.util.Arrays;

/**
 * Scaling workload for Non-Adjacent Max Sum. Input construction happens in prepare, outside the timed region.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {100000, 200000, 400000, 800000, 1600000};

    private Workload() {}

    static Object prepare(int size) {
        int[] values = new int[size];
        Arrays.fill(values, 1);
        return values;
    }

    static void run(Object state) {
        int[] values = (int[]) state;
        long result = Solution.maxSum(values);
        if (result != (values.length + 1) / 2L) {
            throw new IllegalStateException("Unexpected result " + result);
        }
    }

    static int units(int size) {
        return 1;
    }
}
