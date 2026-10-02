package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void measuresABalancedTree() {
        assertThat(Solution.maxDepth(tree(3, 9, 20, null, null, 15, 7))).as("maxDepth of [3, 9, 20, null, null, 15, 7]").isEqualTo(3);
    }

    @Test
    void treatsNullAsDepthZero() {
        assertThat(Solution.maxDepth(null)).as("maxDepth(null)").isEqualTo(0);
    }

    @Test
    void measuresALeftLeaningTree() {
        assertThat(Solution.maxDepth(tree(4, 2, null, 1))).as("maxDepth of [4, 2, null, 1]").isEqualTo(3);
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
