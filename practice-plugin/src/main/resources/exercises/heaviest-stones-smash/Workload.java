package com.jinloes.practice;

/**
 * Scaling workload. Input construction happens in prepare, outside the timed region.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        int[] stones = new int[size];
        long state = 99;
        for (int i = 0; i < size; i++) {
            state = state * 6364136223846793005L + 1442695040888963407L;
            stones[i] = 1 + (int) ((state >>> 33) % 1000);
        }
        return stones;
    }

    static void run(Object state) {
        int left = Solution.smash((int[]) state);
        if (left < 0 || left >= 1000) {
            throw new IllegalStateException("Impossible final weight: " + left);
        }
    }

    static int units(int size) {
        return 1;
    }
}
