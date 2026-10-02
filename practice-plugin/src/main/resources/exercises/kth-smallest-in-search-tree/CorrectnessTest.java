package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void findsEveryRankInAPerfectTree() {
        int[] sorted = {2, 3, 4, 5, 7, 8, 9};
        for (int k = 1; k <= sorted.length; k++) {
            assertThat(Solution.kthSmallest(tree(5, 3, 8, 2, 4, 7, 9), k)).as("k = " + k).isEqualTo(sorted[k - 1]);
        }
    }

    @Test
    void handlesALeftChain() {
        Solution.TreeNode root = tree(3, 2, null, 1);
        assertThat(Solution.kthSmallest(root, 1)).isEqualTo(1);
        assertThat(Solution.kthSmallest(root, 3)).isEqualTo(3);
    }

    @Test
    void handlesARightChain() {
        Solution.TreeNode root = tree(1, null, 2, null, 3);
        assertThat(Solution.kthSmallest(root, 2)).isEqualTo(2);
        assertThat(Solution.kthSmallest(root, 3)).isEqualTo(3);
    }

    @Test
    void handlesExtremeValues() {
        Solution.TreeNode root = tree(0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        assertThat(Solution.kthSmallest(root, 1)).isEqualTo(Integer.MIN_VALUE);
        assertThat(Solution.kthSmallest(root, 2)).isEqualTo(0);
        assertThat(Solution.kthSmallest(root, 3)).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    void handlesNegativeValues() {
        assertThat(Solution.kthSmallest(tree(-5, -10, -1), 2)).isEqualTo(-5);
    }

    @Test
    void findsAnAncestorOfTheLeftSubtree() {
        assertThat(Solution.kthSmallest(tree(8, 4, 12, 2, 6, 10, 14), 4)).isEqualTo(8);
    }

    @Test
    void findsTheMaximum() {
        assertThat(Solution.kthSmallest(tree(8, 4, 12, 2, 6, 10, 14), 7)).isEqualTo(14);
    }

    private static Solution.TreeNode tree(Integer... levels) {
        if (levels.length == 0 || levels[0] == null) {
            return null;
        }
        Solution.TreeNode root = new Solution.TreeNode(levels[0]);
        Deque<Solution.TreeNode> queue = new ArrayDeque<>();
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
