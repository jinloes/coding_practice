package com.jinloes.practice;

/** Scaling workload for Trapped Rainwater; the bars are built outside the timed region. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {16384, 32768, 65536, 131072, 262144};

    private Workload() {}

    static Object prepare(int size) {
        int[] heights = new int[size];
        for (int i = 0; i < size; i++) {
            heights[i] = (int) ((i * 7919L) % 100);
        }
        return heights;
    }

    static void run(Object state) {
        if (Solution.trap((int[]) state) < 0) {
            throw new IllegalStateException("Water volume cannot be negative");
        }
    }

    static int units(int size) {
        return 1;
    }
}
