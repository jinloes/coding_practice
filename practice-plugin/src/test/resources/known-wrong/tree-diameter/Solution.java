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
        if (root == null) {
            return 0;
        }
        return height(root.left) + height(root.right);
    }

    private static int height(TreeNode node) {
        if (node == null) {
            return 0;
        }
        return 1 + Math.max(height(node.left), height(node.right));
    }
}
