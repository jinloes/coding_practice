package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsAnExactSubtree() {
        assertThat(Solution.hasSubtree(tree(10, 6, 12, 4, 8), tree(6, 4, 8))).as("[6, 4, 8] inside [10, 6, 12, 4, 8]").isTrue();
    }

    @Test
    void rejectsAMatchWithExtraDescendants() {
        assertThat(Solution.hasSubtree(tree(10, 6, 12, 4, 8, null, null, null, 5), tree(6, 4, 8))).as("[6, 4, 8] inside a tree where 4 has a child").isFalse();
    }

    @Test
    void findsALeafInsideATree() {
        assertThat(Solution.hasSubtree(tree(1, 1), tree(1))).as("[1] inside [1, 1]").isTrue();
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
