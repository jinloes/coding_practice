package com.jinloes.practice;

final class Workload {
    static final String UNIT = "character";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        String word = "a".repeat(size);
        String[] words = new String[128];
        java.util.Arrays.fill(words, word);
        return words;
    }

    static void run(Object state) {
        if (!Solution.isSorted((String[]) state, "abcdefghijklmnopqrstuvwxyz")) {
            throw new IllegalStateException("Words should be sorted");
        }
    }

    static int units(int size) {
        return 1;
    }
}
