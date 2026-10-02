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

    public static boolean isSameTree(TreeNode a, TreeNode b) {
        if (a == null || b == null) {
            return a == b;
        }
        return a.value == b.value && isSameTree(a.left, b.left) && isSameTree(a.right, b.right);
    }
}
