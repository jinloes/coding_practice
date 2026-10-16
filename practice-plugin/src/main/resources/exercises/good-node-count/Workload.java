package com.jinloes.practice;

/**
 * Scaling workload. Input construction happens in prepare, outside the timed region.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {32768, 65536, 131072, 262144, 524288};

    private Workload() {}

    /** Builds a complete binary tree so the height stays logarithmic and recursion is safe. */
    static Solution.TreeNode complete(int size) {
        Solution.TreeNode[] nodes = new Solution.TreeNode[size];
        for (int i = 0; i < size; i++) {
            nodes[i] = new Solution.TreeNode(i + 1);
        }
        for (int i = 0; i < size; i++) {
            if (2 * i + 1 < size) nodes[i].left = nodes[2 * i + 1];
            if (2 * i + 2 < size) nodes[i].right = nodes[2 * i + 2];
        }
        return nodes[0];
    }

    /** Values increase with the index, so every node is at least as large as its ancestors. */
    static Object prepare(int size) {
        return new Object[]{complete(size), size};
    }

    static void run(Object state) {
        Object[] input = (Object[]) state;
        int good = 0;
        for (int repeat = 0; repeat < 4; repeat++) {
            good = Solution.countGoodNodes((Solution.TreeNode) input[0]);
        }
        if (good != (Integer) input[1]) {
            throw new IllegalStateException("Expected every node to be good but got " + good);
        }
    }

    static int units(int size) {
        return 1;
    }
}
