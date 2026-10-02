package com.jinloes.practice;

/**
 * Scaling workload for Palindromic Substring Count. Input construction happens in prepare, outside the
 * timed region, so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {1000, 2000, 4000, 8000, 16000};

    private Workload() {}

    /** A single repeated letter: every substring is a palindrome, so every center expands fully. */
    static Object prepare(int size) {
        return "a".repeat(size);
    }

    static void run(Object state) {
        String text = (String) state;
        long expected = (long) text.length() * (text.length() + 1) / 2;
        if (Solution.countPalindromes(text) != expected) {
            throw new IllegalStateException("Unexpected palindrome count");
        }
    }

    static int units(int size) {
        return 1;
    }
}
