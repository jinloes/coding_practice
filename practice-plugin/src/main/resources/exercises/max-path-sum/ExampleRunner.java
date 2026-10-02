package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the tree in level order with null for missing children, for example: [5, -4, 6, null, 3]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.maxPathSum(tree(1, 2, 3))).as("Example 1").isEqualTo(6);
        assertThat(Solution.maxPathSum(tree(5, -4, 6, null, 3))).as("Example 2").isEqualTo(11);
        assertThat(Solution.maxPathSum(tree(-7))).as("Example 3").isEqualTo(-7);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        Solution.TreeNode root;
        try {
            root = parseTree(input);
        } catch (NumberFormatException exception) {
            System.out.println("Could not read \"" + input + "\" as a tree. " + USAGE);
            return;
        }
        if (root == null) {
            System.out.println("The tree must have at least one node. " + USAGE);
            return;
        }
        System.out.println("maxPathSum(" + shape(root) + ") = " + Solution.maxPathSum(root));
    }

    private static Solution.TreeNode tree(Integer... levels) {
        if (levels.length == 0 || levels[0] == null) {
            return null;
        }
        Solution.TreeNode root = new Solution.TreeNode(levels[0]);
        java.util.ArrayDeque<Solution.TreeNode> queue = new java.util.ArrayDeque<>();
        queue.add(root);
        int index = 1;
        while (!queue.isEmpty() && index < levels.length) {
            Solution.TreeNode node = queue.poll();
            if (index < levels.length && levels[index] != null) {
                node.left = new Solution.TreeNode(levels[index]);
                queue.add(node.left);
            }
            index++;
            if (index < levels.length && levels[index] != null) {
                node.right = new Solution.TreeNode(levels[index]);
                queue.add(node.right);
            }
            index++;
        }
        return root;
    }

    private static String shape(Solution.TreeNode root) {
        if (root == null) {
            return "[]";
        }
        java.util.List<String> out = new java.util.ArrayList<>();
        java.util.LinkedList<Solution.TreeNode> queue = new java.util.LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty() && out.size() < 100_000) {
            Solution.TreeNode node = queue.poll();
            if (node == null) {
                out.add("null");
            } else {
                out.add(String.valueOf(node.value));
                queue.add(node.left);
                queue.add(node.right);
            }
        }
        while (out.get(out.size() - 1).equals("null")) {
            out.remove(out.size() - 1);
        }
        return "[" + String.join(", ", out) + "]";
    }

    private static Solution.TreeNode parseTree(String text) {
        String cleaned = text.replace('[', ' ').replace(']', ' ').replace(',', ' ').trim();
        if (cleaned.isEmpty()) {
            return null;
        }
        String[] tokens = cleaned.split("\\s+");
        Integer[] levels = new Integer[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            levels[i] = tokens[i].equals("null") ? null : Integer.valueOf(tokens[i]);
        }
        return tree(levels);
    }
}
