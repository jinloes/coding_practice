package com.jinloes.practice;

/**
 * Scaling workload for Longest Common Subsequence. Both strings grow together, so the size is the
 * length of each string.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {512, 1024, 2048, 4096, 8192};

    private Workload() {}

    static Object prepare(int size) {
        return new String[]{build(size, 1L), build(size, 2L)};
    }

    static void run(Object state) {
        String[] pair = (String[]) state;
        int result = Solution.lcsLength(pair[0], pair[1]);
        if (result < 1 || result > pair[0].length()) {
            throw new IllegalStateException("Unexpected subsequence length");
        }
    }

    static int units(int size) {
        return 1;
    }

    private static String build(int length, long seed) {
        StringBuilder text = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            text.append((char) ('a' + (int) ((i * (seed + 1)) % 4)));
        }
        return text.toString();
    }
}
