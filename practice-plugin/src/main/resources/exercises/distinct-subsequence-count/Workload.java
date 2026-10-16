package com.jinloes.practice;

/** Scaling workload for Distinct Subsequence Count; input construction happens outside the timed region. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {256, 512, 1024, 2048, 4096};

    private Workload() {}

    static Object prepare(int size) {
        String letters = "a".repeat(size);
        return new String[]{letters, letters};
    }

    /** Equal strings of one repeated letter have exactly one selection but fill the whole table. */
    static void run(Object state) {
        String[] words = (String[]) state;
        if (Solution.countSubsequences(words[0], words[1]) != 1) {
            throw new IllegalStateException("Expected exactly one selection");
        }
    }

    static int units(int size) {
        return 1;
    }
}
