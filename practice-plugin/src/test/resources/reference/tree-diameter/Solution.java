package com.jinloes.practice;

public class Solution {
    public static class TreeNode {
        public int val;
        public TreeNode left;
        public TreeNode right;

        public TreeNode(int val) {
            this.val = val;
        }
    }

    public static int diameter(TreeNode root) {
        int[] best = new int[1];
        height(root, best);
        return best[0];
    }

    private static int height(TreeNode node, int[] best) {
        if (node == null) {
            return -1;
        }
        int left = height(node.left, best) + 1;
        int right = height(node.right, best) + 1;
        best[0] = Math.max(best[0], left + right);
        return Math.max(left, right);
    }
}
