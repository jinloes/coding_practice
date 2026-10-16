package com.jinloes.practice;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: tree in level order with null for gaps, for example: 4 2 6 1 3 5 7";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.isBalanced(null)).as("Example 1").isEqualTo(true);
        assertThat(Solution.isBalanced(tree(4, 2, 6, 1, 3, 5, 7))).as("Example 2").isEqualTo(true);
        assertThat(Solution.isBalanced(tree(1, 2, null, 3))).as("Example 3").isEqualTo(false);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        try {
            String[] tokens = input.split("\\s+");
            Integer[] levels = new Integer[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                levels[i] = tokens[i].equals("null") ? null : Integer.valueOf(tokens[i]);
            }
            System.out.println("isBalanced = " + Solution.isBalanced(tree(levels)));
        } catch (NumberFormatException exception) {
            System.out.println("Could not parse input. " + USAGE);
        }
    }

    private static Solution.TreeNode tree(Integer... levels) {
        if (levels.length == 0 || levels[0] == null) {
            return null;
        }
        Solution.TreeNode root = new Solution.TreeNode(levels[0]);
        Deque<Solution.TreeNode> pending = new ArrayDeque<>();
        pending.add(root);
        int index = 1;
        while (!pending.isEmpty() && index < levels.length) {
            Solution.TreeNode parent = pending.remove();
            if (index < levels.length && levels[index] != null) {
                parent.left = new Solution.TreeNode(levels[index]);
                pending.add(parent.left);
            }
            index++;
            if (index < levels.length && levels[index] != null) {
                parent.right = new Solution.TreeNode(levels[index]);
                pending.add(parent.right);
            }
            index++;
        }
        return root;
    }

}
