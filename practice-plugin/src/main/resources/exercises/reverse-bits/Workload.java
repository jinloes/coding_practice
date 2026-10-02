package com.jinloes.practice;

/**
 * Scaling workload for Reverse Bits. Input construction happens in prepare, outside the timed region,
 * so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "batch";
    static final int[] SIZES = {8192, 16384, 32768, 65536, 131072, 262144};

    private Workload() {}

    static Object prepare(int size) {
        int[] values = new int[size];
        int seed = 20240611;
        for (int i = 0; i < size; i++) {
            seed = seed * 1664525 + 1013904223;
            values[i] = seed;
        }
        return values;
    }

    /** One batch of calls; the reported cost grows with the number of calls. */
    static void run(Object state) {
        int[] values = (int[]) state;
        for (int value : values) {
            if (Solution.reverseBits(value) != Integer.reverse(value)) {
                throw new IllegalStateException("Unexpected reversal");
            }
        }
    }

    static int units(int size) {
        return 1;
    }
}
