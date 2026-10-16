package com.jinloes.practice;

/** Scaling workload for Minimum Eating Speed; n is the number of piles, each at most 1,000,000 items. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        int[] piles = new int[size];
        for (int i = 0; i < size; i++) {
            piles[i] = 1 + (int) ((i * 7919L) % 1_000_000);
        }
        return new int[][] {piles, {2 * size}};
    }

    static void run(Object state) {
        int[][] data = (int[][]) state;
        int speed = Solution.minimumSpeed(data[0], data[1][0]);
        if (speed < 1 || speed > 1_000_000) {
            throw new IllegalStateException("Unexpected speed " + speed);
        }
    }

    static int units(int size) {
        return 1;
    }
}
