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

    public static TreeNode mirror(TreeNode root) {
        if (root == null) {
            return null;
        }
        TreeNode swap = root.left;
        root.left = root.right;
        root.right = swap;
        mirror(root.left);
        mirror(root.right);
        return root;
    }
}
