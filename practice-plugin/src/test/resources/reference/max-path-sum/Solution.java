package com.jinloes.practice;

public class Solution {
    public static class TreeNode {
        public int value;
        public TreeNode left;
        public TreeNode right;

        public TreeNode(int value) {
            this.value = value;
        }

        public TreeNode(int value, TreeNode left, TreeNode right) {
            this.value = value;
            this.left = left;
            this.right = right;
        }
    }

    public static int maxPathSum(TreeNode root) {
        int[] best = {Integer.MIN_VALUE};
        gain(root, best);
        return best[0];
    }

    private static int gain(TreeNode node, int[] best) {
        if (node == null) {
            return 0;
        }
        int left = Math.max(0, gain(node.left, best));
        int right = Math.max(0, gain(node.right, best));
        best[0] = Math.max(best[0], node.value + left + right);
        return node.value + Math.max(left, right);
    }
}
