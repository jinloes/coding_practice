package com.jinloes.practice;

final class Workload {
    static final String UNIT = "run";
    static final int[] SIZES = {2_048, 4_096, 8_192, 16_384, 32_768};

    private Workload() {
    }

    static Object prepare(int size) {
        String[] words = new String[size];
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        for (int i = 0; i < size; i++) {
            int family = i / 4;
            int step = i % 4;
            char c1 = alphabet[family % 26];
            char c2 = alphabet[(family / 26) % 26];
            char c3 = alphabet[(family / 676) % 26];
            String base = "" + c1 + c2 + c3;
            words[i] = switch (step) {
                case 0 -> base;
                case 1 -> base + "a";
                case 2 -> base + "ab";
                default -> base + "abc";
            };
        }
        return words;
    }

    static void run(Object state) {
        String[] words = (String[]) state;
        int best = new Solution().longestChain(words);
        if (best < 1) {
            throw new IllegalStateException("Unexpected chain length: " + best);
        }
    }

    static int units(int size) {
        return 1;
    }
}
