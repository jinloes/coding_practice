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
        StringBuilder out = new StringBuilder();
        write(root, out);
        return out.toString();
    }

    private static void write(TreeNode node, StringBuilder out) {
        if (node == null) {
            return;
        }
        out.append(node.value).append(',');
        write(node.left, out);
        write(node.right, out);
    }

    public static TreeNode deserialize(String data) {
        TreeNode root = null;
        for (String token : data.split(",")) {
            if (!token.isEmpty()) {
                root = insert(root, Integer.parseInt(token));
            }
        }
        return root;
    }

    private static TreeNode insert(TreeNode node, int value) {
        if (node == null) {
            return new TreeNode(value);
        }
        if (value < node.value) {
            node.left = insert(node.left, value);
        } else {
            node.right = insert(node.right, value);
        }
        return node;
    }
}
