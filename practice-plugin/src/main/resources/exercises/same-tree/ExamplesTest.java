package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void acceptsIdenticalTrees() {
        assertThat(Solution.isSameTree(tree(1, 2, 3), tree(1, 2, 3))).as("[1, 2, 3] vs [1, 2, 3]").isTrue();
    }

    @Test
    void distinguishesLeftFromRightChildren() {
        assertThat(Solution.isSameTree(tree(1, 2), tree(1, null, 2))).as("[1, 2] vs [1, null, 2]").isFalse();
    }

    @Test
    void rejectsADifferentValue() {
        assertThat(Solution.isSameTree(tree(1, 2, 3), tree(1, 2, 4))).as("[1, 2, 3] vs [1, 2, 4]").isFalse();
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
