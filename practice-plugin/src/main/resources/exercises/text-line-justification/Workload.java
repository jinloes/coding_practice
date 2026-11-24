package com.jinloes.practice;

import java.util.List;

final class Workload {
    static final String UNIT = "run";
    static final int[] SIZES = {8_192, 16_384, 32_768, 65_536, 131_072};

    private Workload() {
    }

    static Object prepare(int size) {
        String[] vocab = {"alpha", "beta", "gamma", "delta", "epsilon", "zeta", "eta", "theta"};
        String[] words = new String[size];
        for (int i = 0; i < size; i++) {
            words[i] = vocab[i % vocab.length];
        }
        return words;
    }

    static void run(Object state) {
        String[] words = (String[]) state;
        List<String> lines = new Solution().fullJustify(words, 40);
        if (lines.isEmpty() || lines.get(0).length() != 40) {
            throw new IllegalStateException("Invalid justified output");
        }
    }

    static int units(int size) {
        return 1;
    }
}
