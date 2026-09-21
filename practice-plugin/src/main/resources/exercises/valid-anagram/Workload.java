package com.jinloes.practice;

/**
 * Scaling workload for Valid Anagram. Input construction happens in prepare, outside the timed region,
 * so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {16_000, 32_000, 64_000, 128_000, 256_000};

    private Workload() {}

    static Object prepare(int size) {
        char[] forward = new char[size];
        char[] backward = new char[size];
        for (int i = 0; i < size; i++) {
            forward[i] = (char) ('a' + (i % 26));
            backward[size - 1 - i] = forward[i];
        }
        return new String[]{new String(forward), new String(backward)};
    }

    /** A true anagram, so no early mismatch can cut the scan short. */
    static void run(Object state) {
        String[] words = (String[]) state;
        if (!Solution.isAnagram(words[0], words[1])) {
            throw new IllegalStateException("Probe words are anagrams");
        }
    }

    static int units(int size) {
        return 1;
    }
}
