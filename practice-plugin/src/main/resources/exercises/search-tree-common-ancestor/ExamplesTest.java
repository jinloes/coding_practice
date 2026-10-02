package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void ancestorOfSiblingSubtreesIsTheirParent() {
        Solution.TreeNode root = tree(20, 10, 30, 5, 15, 25, 35);
        assertThat(Solution.lowestCommonAncestor(root, 5, 15)).isSameAs(root.left);
    }

    @Test
    void ancestorAcrossTheRootIsTheRoot() {
        Solution.TreeNode root = tree(20, 10, 30, 5, 15, 25, 35);
        assertThat(Solution.lowestCommonAncestor(root, 5, 35)).isSameAs(root);
    }

    @Test
    void aNodeCanBeItsOwnAncestor() {
        Solution.TreeNode root = tree(20, 10, 30, 5, 15, 25, 35);
        assertThat(Solution.lowestCommonAncestor(root, 10, 15)).isSameAs(root.left);
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
