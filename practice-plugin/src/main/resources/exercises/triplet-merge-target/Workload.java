package com.jinloes.practice;

/** Scaling workload for Triplet Merge Target: mostly unsafe triplets, the three matches come last. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private static final int REPEATS = 32;
    private static final int[] TARGET = {5, 5, 5};

    private Workload() {}

    static Object prepare(int size) {
        int[][] triplets = new int[size][];
        for (int i = 0; i < size - 3; i++) {
            triplets[i] = new int[]{i % 5, 6 + i % 4, i % 3};
        }
        triplets[size - 3] = new int[]{5, 0, 0};
        triplets[size - 2] = new int[]{0, 5, 0};
        triplets[size - 1] = new int[]{0, 0, 5};
        return triplets;
    }

    static void run(Object state) {
        int[][] triplets = (int[][]) state;
        for (int repeat = 0; repeat < REPEATS; repeat++) {
            if (!Solution.canMerge(triplets, TARGET)) {
                throw new IllegalStateException("The last three triplets merge into the target");
            }
        }
    }

    static int units(int size) {
        return REPEATS;
    }
}
