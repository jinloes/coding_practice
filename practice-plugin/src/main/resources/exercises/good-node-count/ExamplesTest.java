package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void countsNodesThatTieOrBeatTheirAncestors() {
        assertThat(Solution.countGoodNodes(tree(6, 2, 7, 6, null, 3, 9))).as("countGoodNodes of [6, 2, 7, 6, null, 3, 9]").isEqualTo(4);
    }

    @Test
    void emptyTreeHasNoGoodNodes() {
        assertThat(Solution.countGoodNodes(null)).as("countGoodNodes(null)").isEqualTo(0);
    }

    @Test
    void onlyTheRootIsGoodWhenValuesShrink() {
        assertThat(Solution.countGoodNodes(tree(9, 5, null, 4))).as("countGoodNodes of [9, 5, null, 4]").isEqualTo(1);
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
