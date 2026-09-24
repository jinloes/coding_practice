package com.jinloes.practice;

/**
 * Scaling workload for Add Strings. Input construction happens in prepare, outside the timed region,
 * so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {16_000, 32_000, 64_000, 128_000, 256_000};

    private Workload() {}

    static Object prepare(int size) {
        String nines = "9".repeat(size);
        return new String[]{nines, new String(nines.toCharArray())};
    }

    /** Every column carries, so the sum gains a digit and no column can be skipped. */
    static void run(Object state) {
        String[] numbers = (String[]) state;
        String sum = Solution.addStrings(numbers[0], numbers[1]);
        if (sum.length() != numbers[0].length() + 1 || sum.charAt(0) != '1' || sum.charAt(sum.length() - 1) != '8') {
            throw new IllegalStateException("Probe sum is 1 followed by nines and a final 8");
        }
    }

    static int units(int size) {
        return 1;
    }
}
