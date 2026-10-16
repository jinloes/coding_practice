package com.jinloes.practice;

/**
 * Scaling workload for Smallest Covering Interval. n is both the number of intervals and the number of queries.
 * Interval i starts at i and spans one to thirteen points; query i asks about point n - 1 - i.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    record Input(int[][] intervals, int[] queries) {}

    static Object prepare(int size) {
        int[][] intervals = new int[size][];
        int[] queries = new int[size];
        for (int i = 0; i < size; i++) {
            intervals[i] = new int[]{i + 1, i + 1 + (i * 7) % 13};
            queries[i] = size - i;
        }
        return new Input(intervals, queries);
    }

    static void run(Object state) {
        Input input = (Input) state;
        int[] answers = Solution.smallestCovering(input.intervals(), input.queries());
        for (int answer : answers) {
            if (answer < 1 || answer > 13) {
                throw new IllegalStateException("Unexpected size " + answer);
            }
        }
    }

    static int units(int size) {
        return 1;
    }
}
