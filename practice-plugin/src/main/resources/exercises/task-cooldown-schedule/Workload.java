package com.jinloes.practice;

/**
 * Scaling workload. Input construction happens in prepare, outside the timed region.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {32768, 65536, 131072, 262144, 524288};

    private Workload() {}

    static Object prepare(int size) {
        char[] letters = new char[size];
        long state = 17;
        for (int i = 0; i < size; i++) {
            state = state * 6364136223846793005L + 1442695040888963407L;
            letters[i] = (char) ('A' + (int) ((state >>> 33) % 26));
        }
        return new String(letters);
    }

    static void run(Object state) {
        String tasks = (String) state;
        int total = 0;
        for (int repeat = 0; repeat < 4; repeat++) {
            total = Solution.minSlots(tasks, 2 + repeat);
        }
        if (total < tasks.length()) {
            throw new IllegalStateException("Schedule shorter than the task count: " + total);
        }
    }

    static int units(int size) {
        return 1;
    }
}
