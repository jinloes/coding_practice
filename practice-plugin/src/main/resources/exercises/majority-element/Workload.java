package com.jinloes.practice;

/**
 * Scaling workload for Majority Element. Input construction happens in prepare, outside the timed
 * region, so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {32_000, 64_000, 128_000, 256_000, 512_000};
    private static final int MAJORITY = 7;

    private Workload() {}

    static Object prepare(int size) {
        int[] numbers = new int[size];
        for (int i = 0; i < size; i++) {
            numbers[i] = i % 2 == 0 ? MAJORITY : -i;
        }
        numbers[size - 1] = MAJORITY;
        return numbers;
    }

    /**
     * The majority alternates with distinct values and only gains its winning margin at the last
     * entry, so the answer cannot be settled before the scan ends.
     */
    static void run(Object state) {
        if (Solution.majorityElement((int[]) state) != MAJORITY) {
            throw new IllegalStateException("Probe majority is " + MAJORITY);
        }
    }

    static int units(int size) {
        return 1;
    }
}
