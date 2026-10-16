package com.jinloes.practice;

/**
 * Scaling workload for Balloon Burst Max. The problem is cubic in the balloon count, so the row
 * holds about size^(2/3) balloons: the work then grows quadratically with the size parameter and
 * the table grows linearly with it.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {512, 1024, 2048, 4096, 8192};

    private Workload() {}

    static Object prepare(int size) {
        int count = (int) Math.round(Math.pow(size, 2.0 / 3.0));
        int[] values = new int[count];
        for (int i = 0; i < count; i++) {
            values[i] = 1 + (i * 37) % 9;
        }
        return values;
    }

    static void run(Object state) {
        if (Solution.maxCoins((int[]) state) <= 0) {
            throw new IllegalStateException("Expected a positive total");
        }
    }

    static int units(int size) {
        return 1;
    }
}
