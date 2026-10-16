package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void emptyTreeIsBalanced() {
        assertThat(Solution.isBalanced(null)).isTrue();
    }

    @Test
    void perfectTreeIsBalanced() {
        assertThat(Solution.isBalanced(tree(4, 2, 6, 1, 3, 5, 7))).as("perfect tree of 7 nodes").isTrue();
    }

    @Test
    void leftLeaningChainIsNot() {
        assertThat(Solution.isBalanced(tree(1, 2, null, 3))).as("chain 1 -> 2 -> 3 down the left").isFalse();
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
