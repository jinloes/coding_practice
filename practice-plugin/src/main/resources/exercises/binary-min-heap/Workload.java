package com.jinloes.practice;

/**
 * Scaling workload for Binary Min Heap. Input construction happens in prepare, outside the timed region,
 * so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "operation";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        return new int[]{size};
    }

    /** Descending inserts force sift-up work, and the drain forces a sift-down for every removal. */
    static void run(Object state) {
        int size = ((int[]) state)[0];
        Solution heap = new Solution();
        for (int i = size; i > 0; i--) {
            heap.add(i);
        }
        for (int i = 0; i < size; i++) {
            heap.removeMin();
        }
        if (!heap.isEmpty()) {
            throw new IllegalStateException("Probe drained every value");
        }
    }

    static int units(int size) {
        return size * 2;
    }
}
