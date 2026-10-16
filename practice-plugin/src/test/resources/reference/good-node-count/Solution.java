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

    public static int countGoodNodes(TreeNode root) {
        return count(root, Integer.MIN_VALUE);
    }

    private static int count(TreeNode node, int highestAbove) {
        if (node == null) {
            return 0;
        }
        int good = node.value >= highestAbove ? 1 : 0;
        int highest = Math.max(highestAbove, node.value);
        return good + count(node.left, highest) + count(node.right, highest);
    }
}
