package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void singleNodeHasDepthOne() {
        assertThat(Solution.maxDepth(tree(-9))).as("maxDepth of [-9]").isEqualTo(1);
    }

    @Test
    void followsTheRightSpine() {
        assertThat(Solution.maxDepth(tree(1, null, 2, null, 3, null, 4))).as("maxDepth of a right-leaning tree").isEqualTo(4);
    }

    @Test
    void picksTheTallerSideWhenLeftIsShallow() {
        assertThat(Solution.maxDepth(tree(1, 2, 3, null, null, 4, null, null, 5))).as("maxDepth of [1, 2, 3, null, null, 4, null, null, 5]").isEqualTo(4);
    }

    @Test
    void picksTheTallerSideWhenRightIsShallow() {
        assertThat(Solution.maxDepth(tree(1, 2, 3, 4, null, null, null, 5))).as("maxDepth of [1, 2, 3, 4, null, null, null, 5]").isEqualTo(4);
    }

    @Test
    void ignoresValues() {
        assertThat(Solution.maxDepth(tree(Integer.MIN_VALUE, 0, Integer.MAX_VALUE, -5))).as("maxDepth with extreme values").isEqualTo(3);
    }

    @Test
    void measuresACompleteTree() {
        assertThat(Solution.maxDepth(complete(15))).as("maxDepth of a complete 15-node tree").isEqualTo(4);
    }

    @Test
    void measuresADeepLeftChain() {
        assertThat(Solution.maxDepth(leftChain(1000))).as("maxDepth of a 1000-node left chain").isEqualTo(1000);
    }

    @Test
    void measuresADeepRightChain() {
        assertThat(Solution.maxDepth(rightChain(1000))).as("maxDepth of a 1000-node right chain").isEqualTo(1000);
    }

    @Test
    void countsNodesNotEdges() {
        assertThat(Solution.maxDepth(tree(7, 7))).as("maxDepth of a two-node tree").isEqualTo(2);
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

    private static Solution.TreeNode leftChain(int size) {
        Solution.TreeNode head = null;
        for (int i = size; i >= 1; i--) {
            head = new Solution.TreeNode(i, head, null);
        }
        return head;
    }

    private static Solution.TreeNode rightChain(int size) {
        Solution.TreeNode head = null;
        for (int i = size; i >= 1; i--) {
            head = new Solution.TreeNode(i, null, head);
        }
        return head;
    }

    private static Solution.TreeNode complete(int size) {
        Solution.TreeNode[] nodes = new Solution.TreeNode[size];
        for (int i = 0; i < size; i++) {
            nodes[i] = new Solution.TreeNode(i + 1);
        }
        for (int i = 0; i < size; i++) {
            if (2 * i + 1 < size) nodes[i].left = nodes[2 * i + 1];
            if (2 * i + 2 < size) nodes[i].right = nodes[2 * i + 2];
        }
        return nodes[0];
    }
}
