package com.jinloes.practice;

import java.util.List;

/**
 * Scaling workload for Word Segmentation. The dictionary is fixed with short words, so the size is
 * the text length. The text ends in a letter no word covers, which forces every position to be
 * examined.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {16384, 32768, 65536, 131072, 262144};
    private static final List<String> DICTIONARY = List.of("a", "aa", "aaa", "ab");

    private Workload() {}

    static Object prepare(int size) {
        return "a".repeat(size - 1) + "c";
    }

    static void run(Object state) {
        if (Solution.canSegment((String) state, DICTIONARY)) {
            throw new IllegalStateException("The text cannot be segmented");
        }
    }

    static int units(int size) {
        return 1;
    }
}
