package com.jinloes.practice;

/**
 * Scaling workload for Tree From Traversals. n is the number of nodes; the tree is perfectly balanced so the
 * reference recursion stays shallow. Traversal arrays are built in prepare, outside the timed region.
 */
final class Workload {
    static final String UNIT = "run";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        int[] inorder = new int[size];
        for (int i = 0; i < size; i++) {
            inorder[i] = i;
        }
        int[] preorder = new int[size];
        fill(preorder, new int[]{0}, 0, size - 1);
        return new int[][]{preorder, inorder};
    }

    private static void fill(int[] preorder, int[] next, int low, int high) {
        if (low > high) {
            return;
        }
        int mid = (low + high) >>> 1;
        preorder[next[0]++] = mid;
        fill(preorder, next, low, mid - 1);
        fill(preorder, next, mid + 1, high);
    }

    static void run(Object state) {
        int[][] arrays = (int[][]) state;
        Solution.TreeNode root = Solution.buildTree(arrays[0], arrays[1]);
        if (root == null || root.value != arrays[0][0] || root.left == null || root.right == null) {
            throw new IllegalStateException("Unexpected tree");
        }
    }

    static int units(int size) {
        return 1;
    }
}
