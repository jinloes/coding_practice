package com.jinloes.practice;

/**
 * Scaling workload for Meeting Conflicts. Input construction happens in prepare, outside the timed region,
 * so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536, 131072};

    private Workload() {}

    static Object prepare(int size) {
        int[][] meetings = new int[size][];
        for (int i = 0; i < size; i++) {
            meetings[i] = new int[]{2 * i, 2 * i + 1};
        }
        shuffle(meetings);
        return meetings;
    }

    /** The calendar is conflict free, so the check cannot stop early. */
    static void run(Object state) {
        if (!Solution.canAttendAll((int[][]) state)) {
            throw new IllegalStateException("Unexpected conflict");
        }
    }

    /** Deterministic shuffle so the solution must really sort. */
    private static void shuffle(int[][] items) {
        int seed = 12345;
        for (int i = items.length - 1; i > 0; i--) {
            seed = seed * 1664525 + 1013904223;
            int j = (seed >>> 1) % (i + 1);
            int[] swap = items[i];
            items[i] = items[j];
            items[j] = swap;
        }
    }

    static int units(int size) {
        return 1;
    }
}
