package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: two level-order trees, root then sub, separated by a semicolon, for example: [10, 6, 12, 4, 8]; [6, 4, 8]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.hasSubtree(tree(10, 6, 12, 4, 8), tree(6, 4, 8))).as("Example 1").isTrue();
        assertThat(Solution.hasSubtree(tree(10, 6, 12, 4, 8, null, null, null, 5), tree(6, 4, 8))).as("Example 2").isFalse();
        assertThat(Solution.hasSubtree(tree(1, 1), tree(1))).as("Example 3").isTrue();
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String[] parts = input.split(";", -1);
        if (parts.length != 2) {
            System.out.println("Could not read \"" + input + "\" as two trees. " + USAGE);
            return;
        }
        Solution.TreeNode root;
        Solution.TreeNode sub;
        try {
            root = parseTree(parts[0]);
            sub = parseTree(parts[1]);
        } catch (NumberFormatException exception) {
            System.out.println("Could not read \"" + input + "\" as two trees. " + USAGE);
            return;
        }
        System.out.println("hasSubtree(" + shape(root) + ", " + shape(sub) + ") = " + Solution.hasSubtree(root, sub));
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
