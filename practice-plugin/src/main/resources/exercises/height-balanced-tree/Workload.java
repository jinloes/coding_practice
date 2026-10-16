package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {1500, 3000, 6000, 12000, 24000};
    private static final int REPEATS = 16;

    private Workload() {}

    static Object prepare(int size) {
        return build(0, size - 1);
    }

    private static Solution.TreeNode build(int low, int high) {
        if (low > high) {
            return null;
        }
        int mid = (int) ((low + high + (((low * 2654435761L + high * 40503L) >>> 13) & 1)) >>> 1);
        Solution.TreeNode node = new Solution.TreeNode(mid);
        node.left = build(low, mid - 1);
        node.right = build(mid + 1, high);
        return node;
    }

    static void run(Object state) {
        Solution.TreeNode root = (Solution.TreeNode) state;
        for (int i = 0; i < REPEATS; i++) {
            if (!Solution.isBalanced(root)) {
                throw new IllegalStateException("A median-built tree is balanced");
            }
        }
    }

    static int units(int size) { return 1; }
}
