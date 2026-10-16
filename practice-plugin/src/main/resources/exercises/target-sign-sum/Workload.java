package com.jinloes.practice;

/** Scaling workload for Target Sign Sum; input construction happens outside the timed region. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {128, 256, 512, 1024, 2048};

    private Workload() {}

    static Object prepare(int size) {
        int[] values = new int[size];
        java.util.Arrays.fill(values, 3);
        return values;
    }

    /** Only the all-plus assignment reaches the full total, so the answer is 1 after filling the whole table. */
    static void run(Object state) {
        int[] values = (int[]) state;
        if (Solution.countAssignments(values, 3 * values.length) != 1) {
            throw new IllegalStateException("Expected exactly one assignment");
        }
    }

    static int units(int size) {
        return 1;
    }
}
