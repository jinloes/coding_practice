package com.jinloes.practice;

/** Scaling workload for Permutation Substring; the strings are built outside the timed region. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {16384, 32768, 65536, 131072, 262144};

    private Workload() {}

    static Object prepare(int size) {
        return new String[]{"a".repeat(size), "abc"};
    }

    /** The text is all 'a', so no window ever matches and the whole text is scanned. */
    static void run(Object state) {
        String[] input = (String[]) state;
        if (Solution.containsPermutation(input[0], input[1])) {
            throw new IllegalStateException("Unexpected match");
        }
    }

    static int units(int size) {
        return 1;
    }
}
