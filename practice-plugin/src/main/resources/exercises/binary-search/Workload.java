package com.jinloes.practice;

/**
 * Scaling workload for Binary Search. Input construction happens in prepare, outside the timed region,
 * so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "search";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        int[] sorted = new int[size];
        for (int i = 0; i < size; i++) {
            sorted[i] = i * 2;
        }
        return sorted;
    }

    /** One search per element, so the reported cost is the cost of a single search. */
    static void run(Object state) {
        int[] sorted = (int[]) state;
        long found = 0;
        for (int i = 0; i < sorted.length; i++) {
            found += Solution.search(sorted, i * 2);
        }
        if (found < 0) {
            throw new IllegalStateException("Unexpected search result");
        }
    }

    static int units(int size) {
        return size;
    }
}
