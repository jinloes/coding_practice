package com.jinloes.practice;

/** Scaling workload for Stock Profit with Cooldown; input construction happens outside the timed region. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        int[] prices = new int[size];
        for (int i = 0; i < size; i++) {
            prices[i] = 10 + (i % 4) * 3;
        }
        return prices;
    }

    /** Each block of four days climbs 10, 13, 16, 19 and drops back, so profit is 9 per block, less cooldown effects. */
    static void run(Object state) {
        if (Solution.maxProfit((int[]) state) <= 0) {
            throw new IllegalStateException("Expected a positive profit");
        }
    }

    static int units(int size) {
        return 1;
    }
}
