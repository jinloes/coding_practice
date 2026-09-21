package com.jinloes.practice;

/**
 * Scaling workload for Merge Sorted Lists. Input construction happens in prepare, outside the timed
 * region, so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {16_000, 32_000, 64_000, 128_000, 256_000};

    private Workload() {}

    /** Both lists are rebuilt for every repetition because merging rewires the nodes it is given. */
    static Object prepare(int size) {
        int half = size / 2;
        Solution.Node evens = null;
        Solution.Node odds = null;
        for (int i = half - 1; i >= 0; i--) {
            evens = new Solution.Node(i * 2, evens);
            odds = new Solution.Node(i * 2 + 1, odds);
        }
        return new Solution.Node[]{evens, odds};
    }

    /** The values alternate between the two lists, so neither list can be attached in one step. */
    static void run(Object state) {
        Solution.Node[] lists = (Solution.Node[]) state;
        if (Solution.merge(lists[0], lists[1]) == null) {
            throw new IllegalStateException("Probe lists are not empty");
        }
    }

    static int units(int size) {
        return 1;
    }
}
