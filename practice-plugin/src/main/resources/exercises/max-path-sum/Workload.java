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

    /** The expected answer is computed bottom-up over the array layout, outside the timed region. */
    static Object prepare(int size) {
        int[] gain = new int[size];
        int best = Integer.MIN_VALUE;
        for (int i = size - 1; i >= 0; i--) {
            int left = 2 * i + 1 < size ? gain[2 * i + 1] : 0;
            int right = 2 * i + 2 < size ? gain[2 * i + 2] : 0;
            gain[i] = 1 + Math.max(left, right);
            best = Math.max(best, 1 + left + right);
        }
        return new Object[] {complete(size, 1), best};
    }

    static void run(Object state) {
        Object[] parts = (Object[]) state;
        int result = Solution.maxPathSum((Solution.TreeNode) parts[0]);
        if (result != (Integer) parts[1]) {
            throw new IllegalStateException("Unexpected path sum " + result);
        }
    }

    static int units(int size) {
        return 1;
    }
}
