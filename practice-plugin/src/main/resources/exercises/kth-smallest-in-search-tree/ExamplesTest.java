package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsTheThirdSmallest() {
        assertThat(Solution.kthSmallest(tree(5, 3, 8, 2, 4, 7, 9), 3)).isEqualTo(4);
    }

    @Test
    void singleNodeIsItsOwnSmallest() {
        assertThat(Solution.kthSmallest(tree(10), 1)).isEqualTo(10);
    }

    @Test
    void findsAValueInASparseTree() {
        assertThat(Solution.kthSmallest(tree(20, 10, 30, null, 15), 2)).isEqualTo(15);
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
