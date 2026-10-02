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
        return covers(root, sub) || hasSubtree(root.left, sub) || hasSubtree(root.right, sub);
    }

    private static boolean covers(TreeNode a, TreeNode b) {
        if (b == null) {
            return true;
        }
        if (a == null) {
            return false;
        }
        return a.value == b.value && covers(a.left, b.left) && covers(a.right, b.right);
    }
}
