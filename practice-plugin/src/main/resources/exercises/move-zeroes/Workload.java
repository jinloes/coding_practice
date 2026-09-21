package com.jinloes.practice;

/**
 * Scaling workload for Move Zeroes. Input construction happens in prepare, outside the timed region,
 * so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {32_000, 64_000, 128_000, 256_000, 512_000};

    private Workload() {}

    /** The array is rebuilt for every repetition because moveZeroes is required to mutate it. */
    static Object prepare(int size) {
        int[] numbers = new int[size];
        for (int i = 0; i < size; i++) {
            numbers[i] = i % 2 == 0 ? 0 : i;
        }
        return numbers;
    }

    /** Half the values are zero, so every nonzero value has to be moved. */
    static void run(Object state) {
        int[] numbers = (int[]) state;
        Solution.moveZeroes(numbers);
        if (numbers[numbers.length - 1] != 0) {
            throw new IllegalStateException("Probe array ends with the zeroes it contained");
        }
    }

    static int units(int size) {
        return 1;
    }
}
