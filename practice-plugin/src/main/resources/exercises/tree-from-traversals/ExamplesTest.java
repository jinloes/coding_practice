package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void rebuildsABalancedTree() {
        assertThat(levels(Solution.buildTree(new int[]{4, 2, 1, 3, 6, 5}, new int[]{1, 2, 3, 4, 5, 6})))
                .as("tree for preorder [4, 2, 1, 3, 6, 5]").isEqualTo("[4, 2, 6, 1, 3, 5]");
    }

    @Test
    void rebuildsASingleNode() {
        assertThat(levels(Solution.buildTree(new int[]{7}, new int[]{7}))).isEqualTo("[7]");
    }

    @Test
    void rebuildsALeftLeaningChain() {
        assertThat(levels(Solution.buildTree(new int[]{1, 2, 3}, new int[]{3, 2, 1}))).isEqualTo("[1, 2, null, 3]");
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
