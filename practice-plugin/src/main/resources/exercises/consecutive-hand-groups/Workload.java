package com.jinloes.practice;

/** Scaling workload for Consecutive Hand Groups: a shuffled run 0..n-1 split into groups of 4. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {8192, 16384, 32768, 65536, 131072};

    private Workload() {}

    static Object prepare(int size) {
        int[] cards = new int[size];
        for (int i = 0; i < size; i++) {
            cards[i] = (int) ((i * 40503L) % size);
        }
        return cards;
    }

    static void run(Object state) {
        if (!Solution.canGroup((int[]) state, 4)) {
            throw new IllegalStateException("The shuffled run splits into groups of 4");
        }
    }

    static int units(int size) {
        return 1;
    }
}
