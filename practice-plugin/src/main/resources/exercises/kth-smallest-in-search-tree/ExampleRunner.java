package com.jinloes.practice;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the tree in level order followed by k, for example: [5, 3, 8, 2, 4, 7, 9] 3";

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
        assertThat(Solution.kthSmallest(tree(5, 3, 8, 2, 4, 7, 9), 3)).isEqualTo(4);
    }

    private static void example2() {
        assertThat(Solution.kthSmallest(tree(10), 1)).isEqualTo(10);
    }

    private static void example3() {
        assertThat(Solution.kthSmallest(tree(20, 10, 30, null, 15), 2)).isEqualTo(15);
    }

    private static void runInput(String input) {
        int end = input.lastIndexOf(']');
        if (end < 0) {
            System.out.println(USAGE);
            return;
        }
        try {
            Solution.TreeNode root = parseTree(input.substring(0, end + 1));
            int k = Integer.parseInt(input.substring(end + 1).trim());
            int count = count(root);
            if (k < 1 || k > count) {
                System.out.println("k must be between 1 and " + count + ". " + USAGE);
                return;
            }
            System.out.println("kthSmallest(" + input.substring(0, end + 1) + ", " + k + ") = " + Solution.kthSmallest(root, k));
        } catch (IllegalArgumentException exception) {
            System.out.println("Could not parse input. " + USAGE);
        }
    }

    private static int count(Solution.TreeNode node) {
        return node == null ? 0 : 1 + count(node.left) + count(node.right);
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
