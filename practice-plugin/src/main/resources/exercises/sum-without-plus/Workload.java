package com.jinloes.practice;

/**
 * Scaling workload for Sum Without Plus. Input construction happens in prepare, outside the timed region,
 * so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "batch";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536, 131072};

    private Workload() {}

    static Object prepare(int size) {
        int[] left = new int[size];
        int[] right = new int[size];
        int seed = 20240611;
        for (int i = 0; i < size; i++) {
            seed = seed * 1664525 + 1013904223;
            left[i] = seed;
            seed = seed * 1664525 + 1013904223;
            right[i] = seed;
        }
        return new int[][]{left, right};
    }

    /** One batch of additions; the reported cost grows with the number of additions. */
    static void run(Object state) {
        int[][] operands = (int[][]) state;
        int[] left = operands[0];
        int[] right = operands[1];
        for (int i = 0; i < left.length; i++) {
            if (Solution.add(left[i], right[i]) != left[i] + right[i]) {
                throw new IllegalStateException("Unexpected sum");
            }
        }
    }

    static int units(int size) {
        return 1;
    }
}
