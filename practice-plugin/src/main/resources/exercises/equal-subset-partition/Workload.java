package com.jinloes.practice;

/** Scaling workload for Equal Subset Partition; input construction happens outside the timed region. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {128, 256, 512, 1024, 2048};

    private Workload() {}

    static Object prepare(int size) {
        int[] values = new int[size];
        java.util.Arrays.fill(values, 8);
        return values;
    }

    /** An even count of eights always splits evenly, but the table still covers every sum. */
    static void run(Object state) {
        if (!Solution.canSplitEvenly((int[]) state)) {
            throw new IllegalStateException("Expected a split");
        }
    }

    static int units(int size) {
        return 1;
    }
}
