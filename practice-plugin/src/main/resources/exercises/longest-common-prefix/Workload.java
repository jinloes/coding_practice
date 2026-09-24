package com.jinloes.practice;

/**
 * Scaling workload for Longest Common Prefix. Input construction happens in prepare, outside the
 * timed region, so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {1_000, 2_000, 4_000, 8_000, 16_000};
    private static final String SHARED = "abcdefghijklmnopqrstuvwxyzabcdef";

    private Workload() {}

    static Object prepare(int size) {
        String[] words = new String[size];
        for (int i = 0; i < size; i++) {
            words[i] = SHARED + (char) ('a' + i % 26);
        }
        return words;
    }

    /**
     * Every word shares a 32-letter prefix and they only disagree after it, so each character of
     * every word is read while the returned prefix stays the same size.
     */
    static void run(Object state) {
        if (Solution.longestCommonPrefix((String[]) state).length() != SHARED.length()) {
            throw new IllegalStateException("Probe words share a 32-letter prefix");
        }
    }

    static int units(int size) {
        return 1;
    }
}
