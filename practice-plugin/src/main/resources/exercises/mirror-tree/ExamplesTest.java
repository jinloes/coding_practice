package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void mirrorsAThreeLevelTree() {
        assertThat(shape(Solution.mirror(tree(5, 2, 8, 1, null, 7, 9)))).as("mirror of [5, 2, 8, 1, null, 7, 9]").isEqualTo("[5, 8, 2, 9, 7, null, 1]");
    }

    @Test
    void movesALeftChildToTheRight() {
        assertThat(shape(Solution.mirror(tree(1, 2)))).as("mirror of [1, 2]").isEqualTo("[1, null, 2]");
    }

    @Test
    void keepsAnEmptyTreeEmpty() {
        assertThat(Solution.mirror(null)).as("mirror(null)").isNull();
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
}
