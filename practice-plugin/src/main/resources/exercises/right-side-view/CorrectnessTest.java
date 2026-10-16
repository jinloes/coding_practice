package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void singleNodeShowsItself() {
        assertThat(Solution.rightSideView(tree(-9))).as("rightSideView of [-9]").containsExactly(-9);
    }

    @Test
    void rightChainShowsEveryNode() {
        assertThat(Solution.rightSideView(tree(1, null, 2, null, 3))).as("rightSideView of right chain [1, null, 2, null, 3]").containsExactly(1, 2, 3);
    }

    @Test
    void deeperLeftSubtreeShowsThroughGaps() {
        assertThat(Solution.rightSideView(tree(1, 2, 3, 4, 5, null, null, 6))).as("rightSideView of [1, 2, 3, 4, 5, null, null, 6]").containsExactly(1, 3, 5, 6);
    }

    @Test
    void perfectTreeShowsRightEdge() {
        assertThat(Solution.rightSideView(tree(1, 2, 3, 4, 5, 6, 7))).as("rightSideView of a perfect tree of 7 nodes").containsExactly(1, 3, 7);
    }

    @Test
    void equalValuesAreStillOnePerLevel() {
        assertThat(Solution.rightSideView(tree(4, 4, 4, 4, null, null, 4))).as("rightSideView of [4, 4, 4, 4, null, null, 4]").containsExactly(4, 4, 4);
    }

    @Test
    void rightNodeWinsOverLeftOnSameLevel() {
        assertThat(Solution.rightSideView(tree(10, 20, 30))).as("rightSideView of [10, 20, 30]").containsExactly(10, 30);
    }

    @Test
    void largePerfectTreeShowsPowersOfTwoMinusOne() {
        int depth = 10;
        Integer[] levels = new Integer[(1 << depth) - 1];
        for (int i = 0; i < levels.length; i++) {
            levels[i] = i + 1;
        }
        List<Integer> expected = new ArrayList<>();
        for (int level = 0; level < depth; level++) {
            expected.add((1 << (level + 1)) - 1);
        }
        assertThat(Solution.rightSideView(tree(levels))).as("rightSideView of a perfect tree of depth 10").containsExactlyElementsOf(expected);
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
