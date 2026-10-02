package com.jinloes.practice;

/**
 * Scaling workload for Fewest Coins. The coin set is fixed, so the size is the amount and the
 * cost grows with the amount alone.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {16384, 32768, 65536, 131072, 262144};
    private static final int[] COINS = {5, 7, 11};

    private Workload() {}

    static Object prepare(int size) {
        return size;
    }

    static void run(Object state) {
        int amount = (Integer) state;
        int result = Solution.minCoins(COINS, amount);
        if (result < amount / 11 || result > amount / 5 + 1) {
            throw new IllegalStateException("Unexpected coin count");
        }
    }

    static int units(int size) {
        return 1;
    }
}
