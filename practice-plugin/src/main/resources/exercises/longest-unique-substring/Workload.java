package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {1 << 18, 1 << 19, 1 << 20, 1 << 21, 1 << 22};

    private Workload() {
    }

    private static final int PERIOD = 100;

    static Object prepare(int size) {
        StringBuilder text = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            text.append((char) ('!' + i % PERIOD));
        }
        return text.toString();
    }

    /** The text repeats a block of 100 distinct characters, so the answer is always 100. */
    static void run(Object state) {
        if (Solution.longestUniqueLength((String) state) != PERIOD) {
            throw new IllegalStateException("Unexpected longest run");
        }
    }

    static int units(int size) {
        return 1;
    }
}
