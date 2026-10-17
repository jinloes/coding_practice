package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {8_000, 16_000, 32_000, 64_000, 128_000};

    private Workload() {
    }

    static Object prepare(int size) {
        int patternPrefix = size / 4;
        StringBuilder text = new StringBuilder(size + 1);
        for (int i = 0; i < size; i++) {
            text.append('a');
        }
        text.append('c');
        String pattern = "a".repeat(patternPrefix) + "c";
        return new String[]{text.toString(), pattern};
    }

    static void run(Object state) {
        String[] parts = (String[]) state;
        if (Solution.substringIndex(parts[0], parts[1]) != parts[0].length() - parts[1].length()) {
            throw new IllegalStateException("Pattern should appear only at the end");
        }
    }

    static int units(int size) {
        return 1;
    }
}
