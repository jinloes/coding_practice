package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void acceptsAValidTree() {
        assertThat(Solution.isValidSearchTree(tree(5, 3, 8, 1, 4))).isTrue();
    }

    @Test
    void rejectsADeepViolation() {
        assertThat(Solution.isValidSearchTree(tree(5, 3, 8, null, null, 4, 9)))
                .as("4 in the right subtree of 5").isFalse();
    }

    @Test
    void rejectsADuplicateChild() {
        assertThat(Solution.isValidSearchTree(tree(2, 2))).isFalse();
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
