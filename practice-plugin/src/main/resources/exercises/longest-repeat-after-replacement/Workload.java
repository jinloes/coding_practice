package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {1 << 18, 1 << 19, 1 << 20, 1 << 21, 1 << 22};

    private Workload() {
    }

    static Object prepare(int size) {
        StringBuilder text = new StringBuilder(size);
        long seed = 12345;
        for (int i = 0; i < size; i++) {
            seed = (seed * 1103515245L + 12345L) & 0x7fffffffL;
            text.append((char) ('A' + (seed >> 8) % 26));
        }
        return text.toString();
    }

    /** Random letters with a fixed k = 3, so the best window stays small while every character is processed. */
    static void run(Object state) {
        String text = (String) state;
        int result = Solution.longestRepeat(text, 3);
        if (result < 4 || result > text.length()) {
            throw new IllegalStateException("Unexpected window length");
        }
    }

    static int units(int size) {
        return 1;
    }
}
