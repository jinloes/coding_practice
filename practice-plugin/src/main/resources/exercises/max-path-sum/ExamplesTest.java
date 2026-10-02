package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void joinsBothChildrenThroughTheRoot() {
        assertThat(Solution.maxPathSum(tree(1, 2, 3))).as("maxPathSum of [1, 2, 3]").isEqualTo(6);
    }

    @Test
    void dropsANegativeBranch() {
        assertThat(Solution.maxPathSum(tree(5, -4, 6, null, 3))).as("maxPathSum of [5, -4, 6, null, 3]").isEqualTo(11);
    }

    @Test
    void returnsTheNegativeValueOfASingleNode() {
        assertThat(Solution.maxPathSum(tree(-7))).as("maxPathSum of [-7]").isEqualTo(-7);
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
}
