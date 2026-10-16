package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

/**
 * Scaling workload for Word Ladder Length. The list holds {@code size} distinct four-letter words
 * (digits of the index in base 26) and the end word is absent, so a correct search has to expand
 * every word before answering 0.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {2048, 4096, 8192, 16384, 32768};

    private Workload() {}

    static Object prepare(int size) {
        List<String> words = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            char[] letters = new char[4];
            int rest = i;
            for (int d = 3; d >= 0; d--) {
                letters[d] = (char) ('a' + rest % 26);
                rest /= 26;
            }
            words.add(new String(letters));
        }
        return words;
    }

    @SuppressWarnings("unchecked")
    static void run(Object state) {
        if (Solution.ladderLength("aaaa", "zzzz", (List<String>) state) != 0) {
            throw new IllegalStateException("zzzz is not listed, so there is no ladder");
        }
    }

    static int units(int size) {
        return 1;
    }
}
