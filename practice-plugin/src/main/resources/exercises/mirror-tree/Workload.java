package com.jinloes.practice;

/**
 * Scaling workload. Input construction happens in prepare, outside the timed region.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {32768, 65536, 131072, 262144, 524288};

    private Workload() {}

    /** Builds a complete binary tree so the height stays logarithmic and recursion is safe. */
    static Solution.TreeNode complete(int size, int ones) {
        Solution.TreeNode[] nodes = new Solution.TreeNode[size];
        for (int i = 0; i < size; i++) {
            nodes[i] = new Solution.TreeNode(ones != 0 ? ones : i + 1);
        }
        for (int i = 0; i < size; i++) {
            if (2 * i + 1 < size) nodes[i].left = nodes[2 * i + 1];
            if (2 * i + 2 < size) nodes[i].right = nodes[2 * i + 2];
        }
        return nodes[0];
    }

    /** Mirroring mutates the tree, so the harness rebuilds it for every repetition. */
    static Object prepare(int size) {
        Solution.TreeNode root = complete(size, 0);
        return new Solution.TreeNode[] {root, root.right};
    }

    static void run(Object state) {
        Solution.TreeNode[] nodes = (Solution.TreeNode[]) state;
        Solution.TreeNode result = Solution.mirror(nodes[0]);
        if (result != nodes[0] || result.left != nodes[1]) {
            throw new IllegalStateException("Root children were not swapped");
        }
    }

    static int units(int size) {
        return 1;
    }
}
