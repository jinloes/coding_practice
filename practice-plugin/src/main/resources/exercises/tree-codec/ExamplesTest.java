package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void roundTripsATreeWithGaps() {
        assertThat(shape(roundTrip(tree(1, 2, 3, null, null, 4, 5)))).as("round trip of [1, 2, 3, null, null, 4, 5]").isEqualTo("[1, 2, 3, null, null, 4, 5]");
    }

    @Test
    void roundTripsAnEmptyTree() {
        assertThat(roundTrip(null)).as("round trip of null").isNull();
    }

    @Test
    void roundTripsNegativeValues() {
        assertThat(shape(roundTrip(tree(-1, -2, null, -3)))).as("round trip of [-1, -2, null, -3]").isEqualTo("[-1, -2, null, -3]");
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

    private static Solution.TreeNode roundTrip(Solution.TreeNode root) {
        String data = Solution.serialize(root);
        assertThat(data).as("serialize result").isNotNull();
        return Solution.deserialize(data);
    }
}
