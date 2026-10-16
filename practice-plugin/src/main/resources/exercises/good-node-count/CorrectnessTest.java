package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void singleNegativeRootIsGood() {
        assertThat(Solution.countGoodNodes(tree(-9))).as("countGoodNodes of [-9]").isEqualTo(1);
    }

    @Test
    void equalValuesAllCount() {
        assertThat(Solution.countGoodNodes(tree(2, 2, 2, 2, 2))).as("countGoodNodes of five equal values").isEqualTo(5);
    }

    @Test
    void increasingChainCountsEveryNode() {
        assertThat(Solution.countGoodNodes(tree(1, null, 2, null, 3, null, 4))).as("countGoodNodes of an increasing right chain").isEqualTo(4);
    }

    @Test
    void negativeValuesCompareCorrectly() {
        assertThat(Solution.countGoodNodes(tree(-4, -6, -5, -7, -4))).as("countGoodNodes of [-4, -6, -5, -7, -4]").isEqualTo(2);
    }

    @Test
    void ancestorsAboveTheParentMatter() {
        assertThat(Solution.countGoodNodes(tree(5, 8, 1, 6, 9))).as("countGoodNodes of [5, 8, 1, 6, 9]").isEqualTo(3);
    }

    @Test
    void siblingsDoNotInfluenceEachOther() {
        assertThat(Solution.countGoodNodes(tree(3, 10, 4, null, null, 5))).as("countGoodNodes of [3, 10, 4, null, null, 5]").isEqualTo(4);
    }

    @Test
    void largePerfectIncreasingTreeIsAllGood() {
        Integer[] levels = new Integer[1023];
        for (int i = 0; i < levels.length; i++) {
            levels[i] = i + 1;
        }
        assertThat(Solution.countGoodNodes(tree(levels))).as("countGoodNodes of an increasing perfect tree of 1023 nodes").isEqualTo(1023);
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
