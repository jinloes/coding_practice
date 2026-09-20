package com.jinloes.practice;

/**
 * Scaling workload for Pair Sum. Input construction happens in prepare, outside the timed region,
 * so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {2000, 4000, 8000, 16000, 32000};

    private Workload() {}

    static Object prepare(int size) {
        int[] numbers = new int[size];
        for (int i = 0; i < size; i++) {
            numbers[i] = i * 2;
        }
        return numbers;
    }

    /** An odd target no pair of even values can reach, so the solution cannot exit early. */
    static void run(Object state) {
        consume(Solution.findPair((int[]) state, 1));
    }

    static int units(int size) {
        return 1;
    }

    private static void consume(int[] result) {
        if (result != null && result.length > 2) {
            throw new IllegalStateException("Unexpected result width");
        }
    }
}
