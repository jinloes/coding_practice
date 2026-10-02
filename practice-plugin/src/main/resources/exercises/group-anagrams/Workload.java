package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

/**
 * Scaling workload for Group Anagrams. Input construction happens in prepare, outside the timed region,
 * so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {10_000, 20_000, 40_000, 80_000, 160_000};
    private static final int WORD_LENGTH = 8;

    private Workload() {}

    /** Fixed-length pseudo-random words over a small alphabet so many of them are anagrams. */
    static Object prepare(int size) {
        List<String> words = new ArrayList<>(size);
        long seed = 12345;
        char[] letters = new char[WORD_LENGTH];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < WORD_LENGTH; j++) {
                seed = seed * 6364136223846793005L + 1442695040888963407L;
                letters[j] = (char) ('a' + (int) ((seed >>> 33) % 5));
            }
            words.add(new String(letters));
        }
        return words;
    }

    @SuppressWarnings("unchecked")
    static void run(Object state) {
        List<String> words = (List<String>) state;
        int total = 0;
        for (List<String> group : Solution.groupAnagrams(words)) {
            total += group.size();
        }
        if (total != words.size()) {
            throw new IllegalStateException("Every word must appear in exactly one group");
        }
    }

    static int units(int size) {
        return 1;
    }
}
