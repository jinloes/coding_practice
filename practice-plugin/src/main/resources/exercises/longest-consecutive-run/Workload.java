package com.jinloes.practice;

/** Scaling workload for Longest Consecutive Run; input construction happens outside the timed region. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        int[] numbers = new int[size];
        for (int i = 0; i < size; i++) {
            numbers[i] = (int) ((i * 7919L) % size);
        }
        return numbers;
    }

    /** A shuffled permutation of 0..size-1, so the whole array is one run. */
    static void run(Object state) {
        int[] numbers = (int[]) state;
        if (Solution.longestRun(numbers) != numbers.length) {
            throw new IllegalStateException("Unexpected run length");
        }
    }

    static int units(int size) {
        return 1;
    }
}
