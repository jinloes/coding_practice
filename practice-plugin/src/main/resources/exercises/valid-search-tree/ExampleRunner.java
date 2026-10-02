package com.jinloes.practice;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the tree in level order with null for missing children, for example: [5, 3, 8, null, null, 4, 9]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        example1();
        example2();
        example3();
        System.out.println("Examples passed: 3");
    }

    private static void example1() {
        assertThat(Solution.isValidSearchTree(tree(5, 3, 8, 1, 4))).isTrue();
    }

    private static void example2() {
        assertThat(Solution.isValidSearchTree(tree(5, 3, 8, null, null, 4, 9)))
                .as("4 in the right subtree of 5").isFalse();
    }

    private static void example3() {
        assertThat(Solution.isValidSearchTree(tree(2, 2))).isFalse();
    }

    private static void runInput(String input) {
        try {
            Solution.TreeNode root = parseTree(input);
            System.out.println("isValidSearchTree(" + input.trim() + ") = " + Solution.isValidSearchTree(root));
        } catch (IllegalArgumentException exception) {
            System.out.println("Could not parse input. " + USAGE);
        }
    }

    private static Solution.TreeNode parseTree(String text) {
        String body = text.trim();
        if (!body.startsWith("[") || !body.endsWith("]")) {
            throw new IllegalArgumentException("not a bracketed list");
        }
        body = body.substring(1, body.length() - 1).trim();
        if (body.isEmpty() || body.startsWith("null")) {
            return null;
        }
        String[] tokens = body.split("\\s*,\\s*");
        Solution.TreeNode root = new Solution.TreeNode(Integer.parseInt(tokens[0]));
        Deque<Solution.TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        int index = 1;
        while (!queue.isEmpty() && index < tokens.length) {
            Solution.TreeNode node = queue.poll();
            if (index < tokens.length) {
                String token = tokens[index++];
                if (!token.equals("null")) {
                    node.left = new Solution.TreeNode(Integer.parseInt(token));
                    queue.add(node.left);
                }
            }
            if (index < tokens.length) {
                String token = tokens[index++];
                if (!token.equals("null")) {
                    node.right = new Solution.TreeNode(Integer.parseInt(token));
                    queue.add(node.right);
                }
            }
        }
        return root;
    }

    private static Solution.TreeNode tree(Integer... levels) {
        if (levels.length == 0 || levels[0] == null) {
            return null;
        }
        Solution.TreeNode root = new Solution.TreeNode(levels[0]);
        Deque<Solution.TreeNode> queue = new ArrayDeque<>();
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
}
