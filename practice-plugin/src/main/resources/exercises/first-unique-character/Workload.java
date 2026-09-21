package com.jinloes.practice;

/**
 * Scaling workload for First Unique Character. Input construction happens in prepare, outside the
 * timed region, so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {16_000, 32_000, 64_000, 128_000, 256_000};

    private Workload() {}

    static Object prepare(int size) {
        char[] characters = new char[size];
        for (int i = 0; i < size; i++) {
            characters[i] = (char) ('a' + (i % 25));
        }
        characters[size - 1] = 'z';
        return new String(characters);
    }

    /** The only unique character sits at the end, so no answer can be returned early. */
    static void run(Object state) {
        String word = (String) state;
        if (Solution.firstUniqueIndex(word) != word.length() - 1) {
            throw new IllegalStateException("Probe word has one unique character at its end");
        }
    }

    static int units(int size) {
        return 1;
    }
}
