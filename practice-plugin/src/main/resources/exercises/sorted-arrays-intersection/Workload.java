package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {16_384, 32_768, 65_536, 131_072, 262_144};
    private static volatile long sink;

    private Workload() {
    }

    static Object prepare(int size) {
        int[] left = new int[size];
        int[] right = new int[size];
        for (int i = 0; i < size; i++) {
            left[i] = i * 2;
            right[i] = i * 2;
        }
        return new int[][]{left, right};
    }

    static void run(Object state) {
        int[][] arrays = (int[][]) state;
        int[] result = Solution.intersection(arrays[0], arrays[1]);
        if (result.length != arrays[0].length) {
            throw new IllegalStateException("Every prepared value should be shared");
        }
        long sum = 0;
        for (int repeat = 0; repeat < 128; repeat++) {
            for (int value : result) {
                sum = (sum * 1_664_525L) + value + repeat;
            }
        }
        sink = sum;
    }

    static int units(int size) {
        return 1;
    }
}
