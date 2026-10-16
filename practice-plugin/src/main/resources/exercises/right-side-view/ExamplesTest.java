package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void seesTheRightmostNodeOfEachLevel() {
        assertThat(Solution.rightSideView(tree(5, 2, 8, null, 4, null, 6))).as("rightSideView of [5, 2, 8, null, 4, null, 6]").containsExactly(5, 8, 6);
    }

    @Test
    void emptyTreeHasNoView() {
        assertThat(Solution.rightSideView(null)).as("rightSideView(null)").isEmpty();
    }

    @Test
    void leftChainIsFullyVisible() {
        assertThat(Solution.rightSideView(tree(7, 8, null, 9))).as("rightSideView of [7, 8, null, 9]").containsExactly(7, 8, 9);
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
