package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void groupsValuesByLevel() {
        assertThat(Solution.levelOrder(tree(1, 2, 3, 4, null, null, 5))).as("levelOrder of [1, 2, 3, 4, null, null, 5]")
                .containsExactly(List.of(1), List.of(2, 3), List.of(4, 5));
    }

    @Test
    void returnsNoLevelsForAnEmptyTree() {
        assertThat(Solution.levelOrder(null)).as("levelOrder(null)").isEmpty();
    }

    @Test
    void returnsOneLevelForASingleNode() {
        assertThat(Solution.levelOrder(tree(7))).as("levelOrder of [7]").containsExactly(List.of(7));
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
