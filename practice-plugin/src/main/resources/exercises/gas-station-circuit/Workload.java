package com.jinloes.practice;

/** Scaling workload for Gas Station Circuit: every station breaks even, repeated for stable timing. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private static final int REPEATS = 32;

    private Workload() {}

    static Object prepare(int size) {
        int[] gain = new int[size];
        int[] cost = new int[size];
        for (int i = 0; i < size; i++) {
            gain[i] = 3 + i % 5;
            cost[i] = 3 + i % 5;
        }
        return new int[][]{gain, cost};
    }

    static void run(Object state) {
        int[][] arrays = (int[][]) state;
        for (int repeat = 0; repeat < REPEATS; repeat++) {
            int start = Solution.startingStation(arrays[0], arrays[1]);
            if (start < 0 || start >= arrays[0].length) {
                throw new IllegalStateException("Expected a valid start, got " + start);
            }
        }
    }

    static int units(int size) {
        return REPEATS;
    }
}
