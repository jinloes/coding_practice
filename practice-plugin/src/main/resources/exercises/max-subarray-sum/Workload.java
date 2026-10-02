package com.jinloes.practice;

/** Scaling workload for Maximum Subarray Sum; input construction happens outside the timed region. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {16384, 32768, 65536, 131072, 262144};

    private Workload() {}

    static Object prepare(int size) {
        int[] numbers = new int[size];
        for (int i = 0; i < size; i++) {
            numbers[i] = (i % 7) - 3;
        }
        return numbers;
    }

    static void run(Object state) {
        if (Solution.maxSubarraySum((int[]) state) < 3) {
            throw new IllegalStateException("Result is smaller than the largest element");
        }
    }

    static int units(int size) {
        return 1;
    }
}
