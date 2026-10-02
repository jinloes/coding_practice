package com.jinloes.practice;

/**
 * Scaling workload for Insert Interval. Input construction happens in prepare, outside the timed region,
 * so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536, 131072};

    private Workload() {}

    static Object prepare(int size) {
        int[][] intervals = new int[size][];
        for (int i = 0; i < size; i++) {
            intervals[i] = new int[]{3 * i, 3 * i + 1};
        }
        return intervals;
    }

    /** The new interval swallows the first half of the list, so the result has size - size / 2 intervals. */
    static void run(Object state) {
        int[][] intervals = (int[][]) state;
        int size = intervals.length;
        int[][] result = Solution.insert(intervals, new int[]{1, 3 * (size / 2)});
        if (result.length != size - size / 2) {
            throw new IllegalStateException("Unexpected result length");
        }
    }

    static int units(int size) {
        return 1;
    }
}
