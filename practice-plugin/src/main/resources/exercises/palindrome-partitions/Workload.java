package com.jinloes.practice;

/**
 * Scaling workload for Palindrome Partitions. The text repeats "abc", which has no palindrome longer than
 * one character, so there is exactly one partition and the cost is dominated by the O(n^2) palindrome
 * table, even though the number of partitions is exponential in general.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {128, 256, 512, 1024, 2048};

    private Workload() {}

    static Object prepare(int size) {
        StringBuilder text = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            text.append((char) ('a' + i % 3));
        }
        return text.toString();
    }

    static void run(Object state) {
        String text = (String) state;
        var result = Solution.partitions(text);
        if (result.size() != 1 || result.get(0).size() != text.length()) {
            throw new IllegalStateException("Unexpected partitions");
        }
    }

    static int units(int size) {
        return 1;
    }
}
