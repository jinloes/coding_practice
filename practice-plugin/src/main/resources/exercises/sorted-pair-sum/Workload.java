package com.jinloes.practice;

/** Scaling workload for Sorted Pair Sum; the array is built outside the timed region. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {16384, 32768, 65536, 131072, 262144};

    private Workload() {}

    static Object prepare(int size) {
        int[] values = new int[size];
        for (int i = 0; i < size; i++) {
            values[i] = 2 * i;
        }
        return values;
    }

    /** Every value is even and the target is odd, so no pair exists and the whole array is scanned. */
    static void run(Object state) {
        if (Solution.findPair((int[]) state, 1).length != 0) {
            throw new IllegalStateException("Unexpected pair");
        }
    }

    static int units(int size) {
        return 1;
    }
}
