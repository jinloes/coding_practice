package com.jinloes.practice;

/**
 * Scaling workload for Sorted Arrays Median; n is the length of each array (evens and odds, so the median
 * is n - 0.5). Each run repeats the query so the logarithmic cost is measurable.
 */
final class Workload {
    static final String UNIT = "query";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private static final int QUERIES = 4096;

    private Workload() {}

    static Object prepare(int size) {
        int[] evens = new int[size];
        int[] odds = new int[size];
        for (int i = 0; i < size; i++) {
            evens[i] = 2 * i;
            odds[i] = 2 * i + 1;
        }
        return new int[][] {evens, odds};
    }

    static void run(Object state) {
        int[][] arrays = (int[][]) state;
        double expected = arrays[0].length - 0.5;
        for (int q = 0; q < QUERIES; q++) {
            double result = (q & 1) == 0
                    ? Solution.median(arrays[0], arrays[1])
                    : Solution.median(arrays[1], arrays[0]);
            if (result != expected) {
                throw new IllegalStateException("Unexpected median " + result);
            }
        }
    }

    static int units(int size) {
        return QUERIES;
    }
}
