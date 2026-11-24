package com.jinloes.practice;

final class Workload {
    static final String UNIT = "run";
    static final int[] SIZES = {4_096, 8_192, 16_384, 32_768, 65_536};
    private static final int SLOTS = 8;

    private Workload() {
    }

    static Object prepare(int size) {
        return size;
    }

    static void run(Object state) {
        int n = (Integer) state;
        Solution arr = new Solution(SLOTS);
        for (int i = 0; i < n; i++) {
            arr.set(i % SLOTS, i + 1);
            arr.snap();
        }
        long sum = 0;
        for (int i = 0; i < n; i++) {
            int snapId = (int) ((i * 7919L) % n);
            sum += arr.get(i % SLOTS, snapId);
        }
        if (sum <= 0 || arr.get(0, 0) != 1) {
            throw new IllegalStateException("Unexpected snapshot values");
        }
    }

    static int units(int size) {
        return 1;
    }
}
