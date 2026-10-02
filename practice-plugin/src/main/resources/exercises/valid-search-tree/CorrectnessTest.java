package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyTreeIsValid() {
        assertThat(Solution.isValidSearchTree(null)).isTrue();
    }

    @Test
    void singleNodeIsValid() {
        assertThat(Solution.isValidSearchTree(tree(0))).isTrue();
    }

    @Test
    void singleMinimumIsValid() {
        assertThat(Solution.isValidSearchTree(tree(Integer.MIN_VALUE))).isTrue();
    }

    @Test
    void rejectsMinimumDuplicatedOnTheLeft() {
        assertThat(Solution.isValidSearchTree(tree(Integer.MIN_VALUE, Integer.MIN_VALUE))).isFalse();
    }

    @Test
    void rejectsMaximumDuplicatedOnTheRight() {
        assertThat(Solution.isValidSearchTree(tree(Integer.MAX_VALUE, null, Integer.MAX_VALUE))).isFalse();
    }

    @Test
    void acceptsExtremeChildren() {
        assertThat(Solution.isValidSearchTree(tree(0, Integer.MIN_VALUE, Integer.MAX_VALUE))).isTrue();
    }

    @Test
    void acceptsMaximumRootWithSmallLeft() {
        assertThat(Solution.isValidSearchTree(tree(Integer.MAX_VALUE, Integer.MIN_VALUE))).isTrue();
    }

    @Test
    void acceptsMinimumRootWithLargeRight() {
        assertThat(Solution.isValidSearchTree(tree(Integer.MIN_VALUE, null, Integer.MAX_VALUE))).isTrue();
    }

    @Test
    void rejectsLeftSubtreeValueAboveRoot() {
        assertThat(Solution.isValidSearchTree(tree(10, 5, 15, 3, 12))).isFalse();
    }

    @Test
    void rejectsRightSubtreeValueBelowRoot() {
        assertThat(Solution.isValidSearchTree(tree(10, 5, 15, null, null, 6, 20))).isFalse();
    }

    @Test
    void rejectsEqualRightChild() {
        assertThat(Solution.isValidSearchTree(tree(3, null, 3))).isFalse();
    }

    @Test
    void acceptsARightChain() {
        assertThat(Solution.isValidSearchTree(tree(1, null, 2, null, 3))).isTrue();
    }

    @Test
    void acceptsAPerfectTree() {
        assertThat(Solution.isValidSearchTree(tree(8, 4, 12, 2, 6, 10, 14))).isTrue();
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
