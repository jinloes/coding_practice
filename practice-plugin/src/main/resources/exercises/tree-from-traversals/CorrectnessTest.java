package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyTraversalsGiveNoTree() {
        assertThat(Solution.buildTree(new int[0], new int[0])).isNull();
    }

    @Test
    void rebuildsARightLeaningChain() {
        assertThat(levels(Solution.buildTree(new int[]{1, 2, 3}, new int[]{1, 2, 3}))).isEqualTo("[1, null, 2, null, 3]");
    }

    @Test
    void rebuildsAZigZag() {
        assertThat(levels(Solution.buildTree(new int[]{1, 3, 2}, new int[]{1, 2, 3}))).isEqualTo("[1, null, 3, 2]");
    }

    @Test
    void rebuildsAPerfectTree() {
        assertThat(levels(Solution.buildTree(new int[]{8, 4, 2, 6, 12, 10, 14}, new int[]{2, 4, 6, 8, 10, 12, 14})))
                .isEqualTo("[8, 4, 12, 2, 6, 10, 14]");
    }

    @Test
    void rebuildsAnUnbalancedTree() {
        assertThat(levels(Solution.buildTree(new int[]{5, 3, 1, 4, 8}, new int[]{1, 3, 4, 5, 8}))).isEqualTo("[5, 3, 8, 1, 4]");
    }

    @Test
    void handlesExtremeAndNegativeValues() {
        assertThat(levels(Solution.buildTree(new int[]{0, Integer.MIN_VALUE, Integer.MAX_VALUE}, new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE})))
                .isEqualTo("[0, -2147483648, 2147483647]");
    }

    @Test
    void rebuildsAnUnsortedValueTree() {
        assertThat(levels(Solution.buildTree(new int[]{9, -3, 7, 20, 15}, new int[]{7, -3, 20, 9, 15})))
                .isEqualTo("[9, -3, 15, 7, 20]");
    }

    @Test
    void doesNotMutateInputs() {
        int[] preorder = {4, 2, 1, 3, 6, 5};
        int[] inorder = {1, 2, 3, 4, 5, 6};
        Solution.buildTree(preorder, inorder);
        assertThat(preorder).containsExactly(4, 2, 1, 3, 6, 5);
        assertThat(inorder).containsExactly(1, 2, 3, 4, 5, 6);
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
