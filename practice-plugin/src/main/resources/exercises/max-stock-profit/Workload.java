package com.jinloes.practice;

/**
 * Scaling workload for Max Stock Profit. Input construction happens in prepare, outside the timed region,
 * so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {32_000, 64_000, 128_000, 256_000, 512_000};

    private Workload() {}

    static Object prepare(int size) {
        int[] prices = new int[size];
        for (int i = 0; i < size; i++) {
            prices[i] = size - i;
        }
        prices[size - 1] = size * 2;
        return prices;
    }

    /** Prices fall until the final day, so the best pair is only known after the whole array is read. */
    static void run(Object state) {
        int[] prices = (int[]) state;
        if (Solution.maxProfit(prices) <= 0) {
            throw new IllegalStateException("Probe prices allow a profit");
        }
    }

    static int units(int size) {
        return 1;
    }
}
