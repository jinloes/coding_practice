package com.jinloes.practice;

/** Scaling workload for Maximum Subarray Product; input construction happens outside the timed region. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {16384, 32768, 65536, 131072, 262144};

    private Workload() {}

    static Object prepare(int size) {
        int[] numbers = new int[size];
        for (int i = 0; i < size; i++) {
            numbers[i] = (i % 5) - 2;
        }
        return numbers;
    }

    /** Zeros split the input into short runs whose best product is 4. */
    static void run(Object state) {
        if (Solution.maxSubarrayProduct((int[]) state) != 4) {
            throw new IllegalStateException("Unexpected maximum product");
        }
    }

    static int units(int size) {
        return 1;
    }
}
