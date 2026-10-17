package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {8_000, 16_000, 32_000, 64_000, 128_000};

    private Workload() {
    }

    static Object prepare(int size) {
        StringBuilder text = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            text.append((char) ('a' + (i % 26)));
        }
        return new String[]{text.toString(), "aeiou"};
    }

    static void run(Object state) {
        String[] parts = (String[]) state;
        if (Solution.removeSelected(parts[0], parts[1]).length() >= parts[0].length()) {
            throw new IllegalStateException("Probe should remove some characters");
        }
    }

    static int units(int size) {
        return 1;
    }
}
