package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {32_000, 64_000, 128_000, 256_000, 512_000};

    private Workload() {
    }

    static Object prepare(int size) {
        char[] chars = new char[size];
        int split = size / 2;
        for (int i = 0; i < split; i++) {
            chars[i] = (i % 5 == 0) ? 'N' : 'Y';
        }
        for (int i = split; i < size; i++) {
            chars[i] = (i % 5 == 0) ? 'Y' : 'N';
        }
        return new String(chars);
    }

    static void run(Object state) {
        String log = (String) state;
        int hour = Solution.bestClosingHour(log);
        if (hour <= 0 || hour >= log.length()) {
            throw new IllegalStateException("Unexpected closing hour: " + hour);
        }
    }

    static int units(int size) {
        return 1;
    }
}
