package com.jinloes.practice;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the preorder list followed by the inorder list, for example: [4, 2, 1, 3, 6, 5] [1, 2, 3, 4, 5, 6]";

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
        assertThat(levels(Solution.buildTree(new int[]{4, 2, 1, 3, 6, 5}, new int[]{1, 2, 3, 4, 5, 6})))
                .as("tree for preorder [4, 2, 1, 3, 6, 5]").isEqualTo("[4, 2, 6, 1, 3, 5]");
    }

    private static void example2() {
        assertThat(levels(Solution.buildTree(new int[]{7}, new int[]{7}))).isEqualTo("[7]");
    }

    private static void example3() {
        assertThat(levels(Solution.buildTree(new int[]{1, 2, 3}, new int[]{3, 2, 1}))).isEqualTo("[1, 2, null, 3]");
    }

    private static void runInput(String input) {
        int split = input.indexOf(']');
        if (!input.startsWith("[") || split < 0) {
            System.out.println(USAGE);
            return;
        }
        try {
            int[] preorder = parseInts(input.substring(0, split + 1));
            int[] inorder = parseInts(input.substring(split + 1));
            if (!sameDistinctValues(preorder, inorder)) {
                System.out.println("Both lists must hold the same distinct values. " + USAGE);
                return;
            }
            System.out.println("buildTree(" + Arrays.toString(preorder) + ", " + Arrays.toString(inorder) + ") = "
                    + levels(Solution.buildTree(preorder, inorder)));
        } catch (IllegalArgumentException exception) {
            System.out.println("Could not parse input. " + USAGE);
        }
    }

    private static int[] parseInts(String text) {
        String trimmed = text.trim();
        if (!trimmed.startsWith("[") || !trimmed.endsWith("]")) {
            throw new IllegalArgumentException("not a bracketed list");
        }
        String body = trimmed.substring(1, trimmed.length() - 1).trim();
        if (body.isEmpty()) {
            return new int[0];
        }
        return Arrays.stream(body.split("\\s*,\\s*")).mapToInt(Integer::parseInt).toArray();
    }

    private static boolean sameDistinctValues(int[] first, int[] second) {
        Set<Integer> values = new HashSet<>();
        for (int value : first) {
            if (!values.add(value)) {
                return false;
            }
        }
        Set<Integer> other = new HashSet<>();
        for (int value : second) {
            other.add(value);
        }
        return first.length == second.length && values.equals(other);
    }

    private static String levels(Solution.TreeNode root) {
        if (root == null) {
            return "[]";
        }
        List<String> out = new ArrayList<>();
        LinkedList<Solution.TreeNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
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
}
