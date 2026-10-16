package com.jinloes.practice;

/** Scaling workload for String Interleaving; input construction happens outside the timed region. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {256, 512, 1024, 2048, 4096};

    private Workload() {}

    static Object prepare(int size) {
        String letters = "x".repeat(size);
        return new String[]{letters, letters, "x".repeat(2 * size)};
    }

    /** Identical letters make every cell of the table reachable, so the whole table is filled. */
    static void run(Object state) {
        String[] words = (String[]) state;
        if (!Solution.isInterleaving(words[0], words[1], words[2])) {
            throw new IllegalStateException("Expected an interleaving");
        }
    }

    static int units(int size) {
        return 1;
    }
}
