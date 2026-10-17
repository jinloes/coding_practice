package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: level-order tree values with null for missing children, for example: [1, 2, 3, 4, 5, null, 7]";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args)); return; }
        Solution.Node first = build("[1, 2, 3]");
        Solution.connect(first);
        assertThat(levels(first)).isEqualTo("1# 2->3#");
        Solution.Node second = build("[1, 2, 3, 4, 5, null, 7]");
        Solution.connect(second);
        assertThat(levels(second)).isEqualTo("1# 2->3# 4->5->7#");
        System.out.println("Examples passed: 2");
    }
    private static void runInput(String input) { try { Solution.Node root = build(input); Solution.connect(root); System.out.println(levels(root)); } catch (RuntimeException exception) { System.out.println(USAGE); } }
    private static Solution.Node build(String text) {
        String body = text.trim();
        if (!body.startsWith("[") || !body.endsWith("]")) throw new IllegalArgumentException();
        body = body.substring(1, body.length() - 1).trim();
        if (body.isEmpty()) return null;
        String[] parts = body.split("\\s*,\\s*");
        List<Solution.Node> nodes = new ArrayList<>();
        for (String part : parts) nodes.add(part.equalsIgnoreCase("null") ? null : new Solution.Node(Integer.parseInt(part)));
        for (int i = 0, child = 1; i < nodes.size() && child < nodes.size(); i++) {
            Solution.Node node = nodes.get(i);
            if (node == null) continue;
            node.left = nodes.get(child++);
            if (child < nodes.size()) node.right = nodes.get(child++);
        }
        return nodes.get(0);
    }
    private static String levels(Solution.Node root) {
        if (root == null) return "";
        List<Solution.Node> level = List.of(root);
        StringBuilder out = new StringBuilder();
        while (!level.isEmpty()) {
            List<Solution.Node> next = new ArrayList<>();
            Solution.Node cursor = level.get(0);
            boolean first = true;
            while (cursor != null) {
                if (!first) out.append("->");
                out.append(cursor.value);
                if (cursor.left != null) next.add(cursor.left);
                if (cursor.right != null) next.add(cursor.right);
                cursor = cursor.next;
                first = false;
            }
            out.append("# ");
            level = next;
        }
        return out.toString().trim();
    }
}
