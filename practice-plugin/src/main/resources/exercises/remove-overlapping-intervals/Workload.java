package com.jinloes.practice;

/**
 * Scaling workload for Remove Overlapping Intervals. Input construction happens in prepare, outside the timed region,
 * so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536, 131072};

    private Workload() {}

    static Object prepare(int size) {
        int[][] intervals = new int[size][];
        for (int i = 0; i < size; i++) {
            intervals[i] = new int[]{i, i + 2};
        }
        shuffle(intervals);
        return intervals;
    }

    /** Interval i is [i, i + 2], so the best selection keeps every other one. */
    static void run(Object state) {
        int[][] intervals = (int[][]) state;
        int size = intervals.length;
        if (Solution.minRemovals(intervals) != size / 2) {
            throw new IllegalStateException("Unexpected removal count");
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
