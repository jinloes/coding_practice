package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void longestPathAvoidsTheRoot() {
        Solution.TreeNode root = new Solution.TreeNode(0);
        Solution.TreeNode pivot = new Solution.TreeNode(1);
        root.left = pivot;
        pivot.left = chain(4, true);
        pivot.right = chain(4, false);
        assertThat(Solution.diameter(root)).as("two arms of 4 nodes under a pivot below the root").isEqualTo(8);
    }

    @Test
    void twoNodes() {
        assertThat(Solution.diameter(tree(1, 2))).isEqualTo(1);
        assertThat(Solution.diameter(tree(1, null, 2))).isEqualTo(1);
    }

    @Test
    void skewedChainCountsEveryEdge() {
        assertThat(Solution.diameter(chain(2000, true))).isEqualTo(1999);
        assertThat(Solution.diameter(chain(2000, false))).isEqualTo(1999);
    }

    @Test
    void perfectTreeUsesTwoLeafToRootPaths() {
        assertThat(Solution.diameter(full(5))).as("perfect tree with 5 levels").isEqualTo(8);
    }

    @Test
    void unbalancedPathsThroughTheRoot() {
        Solution.TreeNode root = new Solution.TreeNode(0);
        root.left = chain(3, true);
        root.right = chain(5, false);
        assertThat(Solution.diameter(root)).isEqualTo(8);
    }

    @Test
    void ignoresNodeValues() {
        assertThat(Solution.diameter(tree(-5, -3, 7, null, 0, -9))).isEqualTo(4);
    }

    @Test
    void leftVersusRightHeavyPivot() {
        Solution.TreeNode root = tree(1, 2, 3, 4, 5, null, null, 6, null, null, 7);
        assertThat(Solution.diameter(root)).as("6-4-2-5-7 has four edges").isEqualTo(4);
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
