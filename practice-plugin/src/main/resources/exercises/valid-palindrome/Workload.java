package com.jinloes.practice;

/**
 * Scaling workload for Valid Palindrome. Input construction happens in prepare, outside the timed
 * region, so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {32_000, 64_000, 128_000, 256_000, 512_000};

    private Workload() {}

    static Object prepare(int size) {
        char[] text = new char[size];
        for (int i = 0; i < size / 2; i++) {
            char character = switch (i % 4) {
                case 0 -> (char) ('a' + (i / 4) % 26);
                case 1 -> ' ';
                case 2 -> (char) ('0' + i % 10);
                default -> ',';
            };
            text[i] = character;
            text[size - 1 - i] = Character.toUpperCase(character);
        }
        if (size % 2 == 1) {
            text[size / 2] = 'm';
        }
        return new String(text);
    }

    /** A true palindrome mixing case, digits, and punctuation, so the scan must reach the middle. */
    static void run(Object state) {
        if (!Solution.isPalindrome((String) state)) {
            throw new IllegalStateException("Probe text is a palindrome");
        }
    }

    static int units(int size) {
        return 1;
    }
}
