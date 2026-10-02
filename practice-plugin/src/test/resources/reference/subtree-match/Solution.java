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

    public static boolean hasSubtree(TreeNode root, TreeNode sub) {
        if (sub == null) {
            return true;
        }
        if (root == null) {
            return false;
        }
        return identical(root, sub) || hasSubtree(root.left, sub) || hasSubtree(root.right, sub);
    }

    private static boolean identical(TreeNode a, TreeNode b) {
        if (a == null || b == null) {
            return a == b;
        }
        return a.value == b.value && identical(a.left, b.left) && identical(a.right, b.right);
    }
}
