package com.jinloes.practice;

/**
 * Scaling workload for Kth Smallest In Search Tree. n is the number of nodes in a balanced search tree holding
 * 0..n-1; k is the median, so about half of the tree is traversed.
 */
final class Workload {
    static final String UNIT = "run";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        return new Object[]{balanced(0, size - 1), size / 2};
    }

    private static Solution.TreeNode balanced(int low, int high) {
        if (low > high) {
            return null;
        }
        int mid = (low + high) >>> 1;
        return new Solution.TreeNode(mid, balanced(low, mid - 1), balanced(mid + 1, high));
    }

    static void run(Object state) {
        Object[] parts = (Object[]) state;
        int k = (Integer) parts[1];
        int result = Solution.kthSmallest((Solution.TreeNode) parts[0], k);
        if (result != k - 1) {
            throw new IllegalStateException("Unexpected rank value " + result);
        }
    }

    static int units(int size) {
        return 1;
    }
}
