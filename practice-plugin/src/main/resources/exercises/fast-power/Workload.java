package com.jinloes.practice;

/**
 * Scaling workload for Fast Power. n is the exponent. One run raises a base just above 1 to the exponent
 * 4000 times, so the cost per call grows with the number of bits of n.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        return size;
    }

    static void run(Object state) {
        int exponent = (Integer) state;
        double total = 0;
        for (int i = 0; i < 4000; i++) {
            total += Solution.power(1.0 + 1e-9 * (i % 7), exponent);
        }
        if (!(total >= 4000.0) || Double.isInfinite(total)) {
            throw new IllegalStateException("Unexpected total " + total);
        }
    }

    static int units(int size) {
        return 4000;
    }
}
