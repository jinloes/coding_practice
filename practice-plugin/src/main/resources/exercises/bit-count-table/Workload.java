package com.jinloes.practice;

/**
 * Scaling workload for Bit Count Table. Input construction happens in prepare, outside the timed region,
 * so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "table";
    static final int[] SIZES = {16384, 32768, 65536, 131072, 262144, 524288};

    private Workload() {}

    static Object prepare(int size) {
        return size;
    }

    static void run(Object state) {
        int n = (Integer) state;
        int[] table = Solution.countBits(n);
        if (table.length != n + 1 || table[n] != Integer.bitCount(n) || table[n / 2] != Integer.bitCount(n / 2)) {
            throw new IllegalStateException("Unexpected table");
        }
    }

    static int units(int size) {
        return 1;
    }
}
