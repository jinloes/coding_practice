package com.jinloes.practice;

/**
 * Scaling workload for Search Tree Common Ancestor. n is the number of nodes in a balanced search tree holding
 * 0..n-1; each run answers n queries, so the per-query cost is the tree height, O(log n).
 */
final class Workload {
    static final String UNIT = "query";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        return new Object[]{balanced(0, size - 1), size};
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
        Solution.TreeNode root = (Solution.TreeNode) parts[0];
        int size = (Integer) parts[1];
        for (int i = 0; i < size; i++) {
            int p = i;
            int q = (int) (((long) i * 7919L + 13L) % size);
            Solution.TreeNode result = Solution.lowestCommonAncestor(root, p, q);
            if (result == null || result.value < Math.min(p, q) || result.value > Math.max(p, q)) {
                throw new IllegalStateException("Unexpected ancestor for " + p + " and " + q);
            }
        }
    }

    static int units(int size) {
        return size;
    }
}
