package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {512, 1_024, 2_048, 4_096, 8_192};
    private static volatile long sink;

    private Workload() {
    }

    static Object prepare(int size) {
        String[] words = new String[size];
        for (int i = 0; i < size; i++) {
            words[i] = encoded(i);
        }
        words[size - 1] = words[size - 2].substring(0, words[size - 2].length() - 1) + "c";
        return words;
    }

    private static String encoded(int value) {
        StringBuilder builder = new StringBuilder(16);
        String[] pairs = {"aa", "bb", "cc", "dd"};
        for (int digit = 0; digit < 8; digit++) {
            builder.append(pairs[value & 3]);
            value >>>= 2;
        }
        return builder.toString();
    }

    static void run(Object state) {
        if (!Solution.hasOneCharacterDifference((String[]) state)) {
            throw new IllegalStateException("Probe words should contain a one-character pair");
        }
        String[] words = (String[]) state;
        long sum = 0;
        for (int repeat = 0; repeat < 128; repeat++) {
            for (String word : words) {
                sum = (sum * 33) + word.charAt(repeat % word.length());
            }
        }
        sink = sum;
    }

    static int units(int size) {
        return 1;
    }
}
