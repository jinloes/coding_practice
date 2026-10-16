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
        int mid = low + (int) (((long) (low * 31 + high * 17 + 7) * 2654435761L >>> 8) % (high - low + 1));
        Solution.TreeNode node = new Solution.TreeNode(mid);
        node.left = build(low, mid - 1);
        node.right = build(mid + 1, high);
        return node;
    }

    static void run(Object state) {
        Solution.TreeNode root = (Solution.TreeNode) state;
        for (int i = 0; i < REPEATS; i++) {
            if (Solution.diameter(root) < 2) {
                throw new IllegalStateException("A tree this large has a diameter of at least 2");
            }
        }
    }

    static int units(int size) { return 1; }
}
