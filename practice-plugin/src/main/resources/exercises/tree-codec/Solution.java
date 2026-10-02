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

    public static String serialize(TreeNode root) {
        throw new UnsupportedOperationException("Implement serialize");
    }

    public static TreeNode deserialize(String data) {
        throw new UnsupportedOperationException("Implement deserialize");
    }
}
