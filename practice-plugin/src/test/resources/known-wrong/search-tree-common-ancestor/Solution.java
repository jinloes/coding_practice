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

    public static TreeNode lowestCommonAncestor(TreeNode root, int p, int q) {
        TreeNode node = root;
        while (node != null) {
            if (p < node.value) {
                node = node.left;
            } else if (p > node.value) {
                node = node.right;
            } else {
                return node;
            }
        }
        return null;
    }
}
