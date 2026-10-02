package com.jinloes.practice;

/** Scaling workload for Largest Water Container; input construction happens outside the timed region. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {16384, 32768, 65536, 131072, 262144};

    private Workload() {}

    static Object prepare(int size) {
        int[] heights = new int[size];
        for (int i = 0; i < size; i++) {
            heights[i] = (i * 37) % 1000 + 1;
        }
        return heights;
    }

    static void run(Object state) {
        if (Solution.maxArea((int[]) state) <= 0) {
            throw new IllegalStateException("Expected a positive area");
        }
    }

    static int units(int size) {
        return 1;
    }
}
