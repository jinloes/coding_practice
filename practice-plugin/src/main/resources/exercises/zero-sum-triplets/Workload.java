package com.jinloes.practice;

/** Scaling workload for Zero-Sum Triplets; input construction happens outside the timed region. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {512, 1024, 2048, 4096, 8192};

    private Workload() {}

    static Object prepare(int size) {
        int[] numbers = new int[size];
        for (int i = 0; i < size / 2; i++) {
            numbers[2 * i] = 3 * i + 1;
            numbers[2 * i + 1] = -(3 * i + 1);
        }
        return numbers;
    }

    /**
     * Positives are 1 mod 3 and negatives are 2 mod 3, so no three values sum to zero: the
     * answer is empty but every candidate pair still has to be considered.
     */
    static void run(Object state) {
        if (!Solution.findTriplets((int[]) state).isEmpty()) {
            throw new IllegalStateException("Unexpected triplet");
        }
    }

    static int units(int size) {
        return 1;
    }
}
