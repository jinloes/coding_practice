package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void sameValueTwiceIsThatNode() {
        Solution.TreeNode root = tree(20, 10, 30, 5, 15, 25, 35);
        assertThat(Solution.lowestCommonAncestor(root, 25, 25)).isSameAs(root.right.left);
    }

    @Test
    void rootWithItselfIsTheRoot() {
        Solution.TreeNode root = tree(20, 10, 30, 5, 15, 25, 35);
        assertThat(Solution.lowestCommonAncestor(root, 20, 20)).isSameAs(root);
    }

    @Test
    void argumentOrderDoesNotMatter() {
        Solution.TreeNode root = tree(20, 10, 30, 5, 15, 25, 35);
        assertThat(Solution.lowestCommonAncestor(root, 35, 5)).isSameAs(root);
        assertThat(Solution.lowestCommonAncestor(root, 15, 5)).isSameAs(root.left);
    }

    @Test
    void bothInTheRightSubtree() {
        Solution.TreeNode root = tree(20, 10, 30, 5, 15, 25, 35);
        assertThat(Solution.lowestCommonAncestor(root, 25, 35)).isSameAs(root.right);
    }

    @Test
    void walksDownARightChain() {
        Solution.TreeNode root = tree(5, null, 10, null, 15);
        assertThat(Solution.lowestCommonAncestor(root, 10, 15)).isSameAs(root.right);
        assertThat(Solution.lowestCommonAncestor(root, 5, 15)).isSameAs(root);
    }

    @Test
    void handlesExtremeValues() {
        Solution.TreeNode root = tree(0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        assertThat(Solution.lowestCommonAncestor(root, Integer.MIN_VALUE, Integer.MAX_VALUE)).isSameAs(root);
        assertThat(Solution.lowestCommonAncestor(root, Integer.MIN_VALUE, Integer.MIN_VALUE)).isSameAs(root.left);
    }

    @Test
    void findsADeepAncestorOnTheLeft() {
        Solution.TreeNode root = tree(8, 4, 12, 2, 6, 10, 14);
        assertThat(Solution.lowestCommonAncestor(root, 2, 6)).isSameAs(root.left);
    }

    @Test
    void leafAndItsParentGiveTheParent() {
        Solution.TreeNode root = tree(8, 4, 12, 2, 6, 10, 14);
        assertThat(Solution.lowestCommonAncestor(root, 2, 4)).isSameAs(root.left);
    }

    @Test
    void handlesNegativeValues() {
        Solution.TreeNode root = tree(-5, -10, 3, -12, -7);
        assertThat(Solution.lowestCommonAncestor(root, -12, -7)).isSameAs(root.left);
        assertThat(Solution.lowestCommonAncestor(root, -7, 3)).isSameAs(root);
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
