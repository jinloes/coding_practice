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

    public static boolean isValidSearchTree(TreeNode root) {
        if (root == null) {
            return true;
        }
        if (root.left != null && root.left.value >= root.value) {
            return false;
        }
        if (root.right != null && root.right.value <= root.value) {
            return false;
        }
        return isValidSearchTree(root.left) && isValidSearchTree(root.right);
    }
}
