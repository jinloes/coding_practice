package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void singleNodeIsBalanced() {
        assertThat(Solution.isBalanced(tree(1))).isTrue();
    }

    @Test
    void oneChildIsStillBalanced() {
        assertThat(Solution.isBalanced(tree(1, 2))).isTrue();
        assertThat(Solution.isBalanced(tree(1, null, 2))).isTrue();
    }

    @Test
    void rootIsBalancedButADeeperNodeIsNot() {
        Solution.TreeNode root = new Solution.TreeNode(0);
        Solution.TreeNode left = new Solution.TreeNode(1);
        left.left = chain(2, true);
        root.left = left;
        root.right = chain(3, false);
        assertThat(Solution.isBalanced(root)).as("both root subtrees have height 3, but node 1 is lopsided").isFalse();
    }

    @Test
    void heightsDifferingByExactlyOneAreAllowed() {
        Solution.TreeNode root = new Solution.TreeNode(0);
        root.left = chain(2, true);
        root.right = new Solution.TreeNode(9);
        assertThat(Solution.isBalanced(root)).isTrue();
    }

    @Test
    void heightsDifferingByTwoAreNot() {
        Solution.TreeNode root = new Solution.TreeNode(0);
        root.left = chain(3, true);
        root.right = new Solution.TreeNode(9);
        assertThat(Solution.isBalanced(root)).isFalse();
    }

    @Test
    void perfectTreeIsBalancedAtEveryDepth() {
        assertThat(Solution.isBalanced(full(12))).isTrue();
    }

    @Test
    void longChainIsNotBalanced() {
        assertThat(Solution.isBalanced(chain(2000, false))).isFalse();
    }

    @Test
    void imbalanceInTheRightSubtree() {
        Solution.TreeNode root = tree(1, 2, 3, 4, 5, 6, 7);
        root.right.right.right = new Solution.TreeNode(8);
        root.right.right.right.left = new Solution.TreeNode(9);
        assertThat(Solution.isBalanced(root)).as("node 7 has a one-sided chain of two below it").isFalse();
    }

    private static Solution.TreeNode tree(Integer... levels) {
        if (levels.length == 0 || levels[0] == null) {
            return null;
        }
        Solution.TreeNode root = new Solution.TreeNode(levels[0]);
        Deque<Solution.TreeNode> pending = new ArrayDeque<>();
        pending.add(root);
        int index = 1;
        while (!pending.isEmpty() && index < levels.length) {
            Solution.TreeNode parent = pending.remove();
            if (index < levels.length && levels[index] != null) {
                parent.left = new Solution.TreeNode(levels[index]);
                pending.add(parent.left);
            }
            index++;
            if (index < levels.length && levels[index] != null) {
                parent.right = new Solution.TreeNode(levels[index]);
                pending.add(parent.right);
            }
            index++;
        }
        return root;
    }

    private static Solution.TreeNode chain(int length, boolean leftward) {
        Solution.TreeNode root = new Solution.TreeNode(0);
        Solution.TreeNode tail = root;
        for (int i = 1; i < length; i++) {
            Solution.TreeNode next = new Solution.TreeNode(i);
            if (leftward) {
                tail.left = next;
            } else {
                tail.right = next;
            }
            tail = next;
        }
        return root;
    }

    private static Solution.TreeNode full(int depth) {
        if (depth == 0) {
            return null;
        }
        Solution.TreeNode node = new Solution.TreeNode(depth);
        node.left = full(depth - 1);
        node.right = full(depth - 1);
        return node;
    }
}
