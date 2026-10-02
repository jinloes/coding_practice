package com.jinloes.practice;

/**
 * Scaling workload for Digit Decodings. Input construction happens in prepare, outside the timed region.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {300000, 600000, 1200000, 2400000, 4800000};

    private Workload() {}

    static Object prepare(int size) {
        return "103".repeat(size / 3);
    }

    static void run(Object state) {
        int result = Solution.count((String) state);
        if (result != 1) {
            throw new IllegalStateException("Unexpected result " + result);
        }
    }

    static int units(int size) {
        return 1;
    }
}
