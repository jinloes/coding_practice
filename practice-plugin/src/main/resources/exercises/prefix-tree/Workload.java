package com.jinloes.practice;

/**
 * Scaling workload for Prefix Tree. n is the number of distinct words, each exactly 8 letters long, so the
 * total number of letters (and the per-word cost) grows linearly with n. Each run inserts every word and then
 * checks membership and a 4-letter prefix for each one.
 */
final class Workload {
    static final String UNIT = "run";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        String[] words = new String[size];
        for (int i = 0; i < size; i++) {
            char[] letters = new char[8];
            int rest = i * 3 + 1;
            for (int j = 7; j >= 0; j--) {
                letters[j] = (char) ('a' + rest % 26);
                rest /= 26;
            }
            words[i] = new String(letters);
        }
        return words;
    }

    static void run(Object state) {
        String[] words = (String[]) state;
        Solution trie = new Solution();
        for (String word : words) {
            trie.insert(word);
        }
        for (String word : words) {
            if (!trie.contains(word) || !trie.hasPrefix(word.substring(0, 4))) {
                throw new IllegalStateException("Missing inserted word " + word);
            }
        }
        if (trie.contains("zzzzzzzzz")) {
            throw new IllegalStateException("Unexpected word");
        }
    }

    static int units(int size) {
        return 1;
    }
}
