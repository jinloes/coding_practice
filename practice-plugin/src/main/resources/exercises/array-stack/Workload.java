package com.jinloes.practice;

/**
 * Scaling workload for Array Stack. Input construction happens in prepare, outside the timed region,
 * so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "operation";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        return new int[]{size};
    }

    /** Fills and drains a fresh stack, so growth and amortized cost are both included. */
    static void run(Object state) {
        int size = ((int[]) state)[0];
        Solution stack = new Solution();
        for (int i = 0; i < size; i++) {
            stack.push(i);
        }
        for (int i = 0; i < size; i++) {
            stack.pop();
        }
        if (!stack.isEmpty()) {
            throw new IllegalStateException("Probe drained every value");
        }
    }

    static int units(int size) {
        return size * 2;
    }
}
