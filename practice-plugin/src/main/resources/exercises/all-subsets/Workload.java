package com.jinloes.practice;

/**
 * Scaling workload for All Subsets. The size is the number of subsets n = 2^k returned for k distinct
 * values, so the output (and the work) grows by one value each time n doubles.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {1024, 2048, 4096, 8192, 16384, 32768};

    private Workload() {}

    static Object prepare(int size) {
        int[] values = new int[Integer.numberOfTrailingZeros(size)];
        for (int i = 0; i < values.length; i++) {
            values[i] = i + 1;
        }
        return values;
    }

    static void run(Object state) {
        int[] values = (int[]) state;
        if (Solution.subsets(values).size() != 1 << values.length) {
            throw new IllegalStateException("Unexpected subset count");
        }
    }

    static int units(int size) {
        return 1;
    }
}
