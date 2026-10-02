package com.jinloes.practice;

/**
 * Scaling workload for Longest Palindromic Substring. Input construction happens in prepare, outside the
 * timed region, so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {1000, 2000, 4000, 8000, 16000};

    private Workload() {}

    /**
     * A leading "b" followed by repeated "a": every center expands about as far as the string allows, which is
     * the quadratic case for expand-around-center, and the answer is a long substring of length size - 1.
     */
    static Object prepare(int size) {
        return "b" + "a".repeat(size - 1);
    }

    static void run(Object state) {
        String text = (String) state;
        String result = Solution.longestPalindrome(text);
        if (result == null || result.length() != text.length() - 1) {
            throw new IllegalStateException("Unexpected longest palindrome length");
        }
    }

    static int units(int size) {
        return 1;
    }
}
