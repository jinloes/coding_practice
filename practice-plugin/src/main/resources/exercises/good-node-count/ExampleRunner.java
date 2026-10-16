package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the tree in level order with null for missing children, for example: [6, 2, 7, 6, null, 3, 9]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.countGoodNodes(tree(6, 2, 7, 6, null, 3, 9))).as("Example 1").isEqualTo(4);
        assertThat(Solution.countGoodNodes(null)).as("Example 2").isEqualTo(0);
        assertThat(Solution.countGoodNodes(tree(9, 5, null, 4))).as("Example 3").isEqualTo(1);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        Solution.TreeNode root;
        try {
            root = parseTree(input);
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\" as a tree. " + USAGE);
            return;
        }
        System.out.println("countGoodNodes = " + Solution.countGoodNodes(root));
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
