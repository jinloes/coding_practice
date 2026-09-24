package com.jinloes.practice;

/**
 * Scaling workload for Missing Number. Input construction happens in prepare, outside the timed
 * region, so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {16_384, 32_768, 65_536, 131_072, 262_144};
    private static final int CALLS_PER_RUN = 16;

    private Workload() {}

    static Object prepare(int size) {
        int[] numbers = new int[size];
        int next = 0;
        for (int value = size; value >= 0; value--) {
            if (value != size / 3) {
                numbers[next++] = value;
            }
        }
        return numbers;
    }

    /**
     * The values run in reverse with a gap a third of the way in, so the answer is not at either end.
     * One scan is too brief to time reliably, so each run repeats it on the same cache-resident array.
     */
    static void run(Object state) {
        int[] numbers = (int[]) state;
        for (int call = 0; call < CALLS_PER_RUN; call++) {
            if (Solution.missingNumber(numbers) != numbers.length / 3) {
                throw new IllegalStateException("Probe range is missing a third of its length");
            }
        }
    }

    static int units(int size) {
        return 1;
    }
}
