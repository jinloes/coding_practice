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
            out.append("#,");
            return;
        }
        out.append(node.value).append(',');
        write(node.left, out);
        write(node.right, out);
    }

    public static TreeNode deserialize(String data) {
        String[] tokens = data.split(",");
        return read(tokens, new int[] {0});
    }

    private static TreeNode read(String[] tokens, int[] position) {
        String token = tokens[position[0]++];
        if (token.equals("#")) {
            return null;
        }
        TreeNode node = new TreeNode(Integer.parseInt(token));
        node.left = read(tokens, position);
        node.right = read(tokens, position);
        return node;
    }
}
