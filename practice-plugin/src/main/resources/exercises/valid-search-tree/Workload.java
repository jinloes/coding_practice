package com.jinloes.practice;

/**
 * Scaling workload for Valid Search Tree. n is the number of nodes in a perfectly balanced, valid search tree
 * built in prepare, so the timed work is a fixed number of full validation passes.
 */
final class Workload {
    static final String UNIT = "run";
    static final int[] SIZES = {3000, 6000, 12000, 24000};

    private static final int PASSES = 16;

    private Workload() {}

    static Object prepare(int size) {
        return balanced(0, size - 1);
    }

    private static Solution.TreeNode balanced(int low, int high) {
        if (low > high) {
            return null;
        }
        int mid = (low + high) >>> 1;
        return new Solution.TreeNode(mid, balanced(low, mid - 1), balanced(mid + 1, high));
    }

    static void run(Object state) {
        for (int pass = 0; pass < PASSES; pass++) {
            if (!Solution.isValidSearchTree((Solution.TreeNode) state)) {
                throw new IllegalStateException("The tree is a valid search tree");
            }
        }
    }

    static int units(int size) {
        return 1;
    }
}
