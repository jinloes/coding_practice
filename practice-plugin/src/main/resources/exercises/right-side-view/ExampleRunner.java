package com.jinloes.practice;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the tree in level order with null for missing children, for example: [5, 2, 8, null, 4, null, 6]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.rightSideView(tree(5, 2, 8, null, 4, null, 6))).as("Example 1").containsExactly(5, 8, 6);
        assertThat(Solution.rightSideView(null)).as("Example 2").isEmpty();
        assertThat(Solution.rightSideView(tree(7, 8, null, 9))).as("Example 3").containsExactly(7, 8, 9);
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
        List<Integer> view = Solution.rightSideView(root);
        System.out.println("rightSideView = " + view);
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
