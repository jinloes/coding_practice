package com.jinloes.practice;

/** Scaling workload for Largest Histogram Rectangle; n is the number of bars. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        int[] heights = new int[size];
        for (int i = 0; i < size; i++) {
            heights[i] = 1 + (int) ((i * 7919L) % 1000);
        }
        return heights;
    }

    static void run(Object state) {
        if (Solution.largestRectangle((int[]) state) < 1000) {
            throw new IllegalStateException("Unexpectedly small area");
        }
    }

    static int units(int size) {
        return 1;
    }
}
