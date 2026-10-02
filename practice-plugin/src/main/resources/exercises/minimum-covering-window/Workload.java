package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {1 << 18, 1 << 19, 1 << 20, 1 << 21, 1 << 22};

    private Workload() {
    }

    private static final String REQUIRED = "aaz";

    /** The source cycles through the alphabet, so the best window is always 27 characters long. */
    static Object prepare(int size) {
        StringBuilder text = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            text.append((char) ('a' + i % 26));
        }
        return text.toString();
    }

    static void run(Object state) {
        String window = Solution.minimumWindow((String) state, REQUIRED);
        if (window.length() != 27) {
            throw new IllegalStateException("Unexpected window length " + window.length());
        }
    }

    static int units(int size) {
        return 1;
    }
}
