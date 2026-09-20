package com.jinloes.practice;

/**
 * Scaling workload for Balanced Delimiters. Input construction happens in prepare, outside the timed region,
 * so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {2000, 4000, 8000, 16000, 32000};

    private Workload() {}

    static Object prepare(int size) {
        char[] characters = new char[size * 2];
        for (int i = 0; i < size; i++) {
            characters[i] = '(';
            characters[characters.length - 1 - i] = ')';
        }
        return new String(characters);
    }

    /** A fully nested string, so every delimiter must be tracked before any can be closed. */
    static void run(Object state) {
        if (!Solution.isBalanced((String) state)) {
            throw new IllegalStateException("Probe input is balanced");
        }
    }

    static int units(int size) {
        return 1;
    }
}
