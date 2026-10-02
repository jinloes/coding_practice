package com.jinloes.practice;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the tree in level order followed by the two values, for example: [20, 10, 30, 5, 15, 25, 35] 5 15";

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
        Solution.TreeNode root = tree(20, 10, 30, 5, 15, 25, 35);
        assertThat(Solution.lowestCommonAncestor(root, 5, 15)).isSameAs(root.left);
    }

    private static void example2() {
        Solution.TreeNode root = tree(20, 10, 30, 5, 15, 25, 35);
        assertThat(Solution.lowestCommonAncestor(root, 5, 35)).isSameAs(root);
    }

    private static void example3() {
        Solution.TreeNode root = tree(20, 10, 30, 5, 15, 25, 35);
        assertThat(Solution.lowestCommonAncestor(root, 10, 15)).isSameAs(root.left);
    }

    private static void runInput(String input) {
        int end = input.lastIndexOf(']');
        if (end < 0) {
            System.out.println(USAGE);
            return;
        }
        try {
            Solution.TreeNode root = parseTree(input.substring(0, end + 1));
            String[] values = input.substring(end + 1).trim().split("\\s+");
            if (values.length != 2) {
                System.out.println("Need exactly two values. " + USAGE);
                return;
            }
            int p = Integer.parseInt(values[0]);
            int q = Integer.parseInt(values[1]);
            if (!contains(root, p) || !contains(root, q)) {
                System.out.println("Both values must occur in the tree. " + USAGE);
                return;
            }
            Solution.TreeNode result = Solution.lowestCommonAncestor(root, p, q);
            System.out.println("lowestCommonAncestor(" + input.substring(0, end + 1) + ", " + p + ", " + q + ") = "
                    + (result == null ? "null" : "node " + result.value));
        } catch (IllegalArgumentException exception) {
            System.out.println("Could not parse input. " + USAGE);
        }
    }

    private static boolean contains(Solution.TreeNode node, int value) {
        while (node != null && node.value != value) {
            node = value < node.value ? node.left : node.right;
        }
        return node != null;
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
