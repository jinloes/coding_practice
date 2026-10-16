package com.jinloes.practice;

/** Scaling workload for Coin Change Ways; input construction happens outside the timed region. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    private static final int[] COINS = {1, 2};

    static Object prepare(int size) {
        return size;
    }

    /** With coins 1 and 2 there are amount / 2 + 1 combinations. */
    static void run(Object state) {
        int amount = (Integer) state;
        if (Solution.countWays(amount, COINS) != amount / 2 + 1) {
            throw new IllegalStateException("Wrong combination count");
        }
    }

    static int units(int size) {
        return 1;
    }
}
