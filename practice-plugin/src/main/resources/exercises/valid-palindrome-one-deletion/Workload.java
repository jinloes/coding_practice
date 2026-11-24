package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {32_000, 64_000, 128_000, 256_000, 512_000};

    private Workload() {
    }

    static Object prepare(int size) {
        char[] chars = new char[size + 1];
        int half = size / 2;
        for (int i = 0; i < half; i++) {
            char c = (char) ('a' + (i % 25));
            chars[i] = c;
            chars[size - i] = c;
        }
        chars[half] = 'z';
        return new String(chars);
    }

    static void run(Object state) {
        if (!Solution.canBePalindrome((String) state)) {
            throw new IllegalStateException("Probe input is valid with one deletion");
        }
    }

    static int units(int size) {
        return 1;
    }
}
