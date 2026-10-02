package com.jinloes.practice;

/**
 * Scaling workload for Wildcard Word Dictionary. n is the number of distinct words, each exactly 8 letters long.
 * Each run adds every word and then matches one pattern per word that has a single wildcard, so each lookup
 * fans out over at most 26 branches and the total work grows linearly with n.
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
        Solution dictionary = new Solution();
        for (String word : words) {
            dictionary.add(word);
        }
        for (String word : words) {
            String pattern = word.substring(0, 6) + "." + word.charAt(7);
            if (!dictionary.matches(pattern)) {
                throw new IllegalStateException("Pattern " + pattern + " should match " + word);
            }
        }
        if (dictionary.matches("aaaaaaaaa")) {
            throw new IllegalStateException("Unexpected match");
        }
    }

    static int units(int size) {
        return 1;
    }
}
