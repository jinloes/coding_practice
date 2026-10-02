package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void allNegativeTreePicksTheLargestSingleNode() {
        assertThat(Solution.maxPathSum(tree(-3, -2, -5))).as("maxPathSum of [-3, -2, -5]").isEqualTo(-2);
    }

    @Test
    void zeroIsAValidAnswer() {
        assertThat(Solution.maxPathSum(tree(0))).as("maxPathSum of [0]").isEqualTo(0);
    }

    @Test
    void bestPathMayAvoidTheRoot() {
        assertThat(Solution.maxPathSum(tree(-20, 10, -30, 8, 9))).as("maxPathSum of [-20, 10, -30, 8, 9]").isEqualTo(27);
    }

    @Test
    void pathMayUseOnlyOneSideOfTheRoot() {
        assertThat(Solution.maxPathSum(tree(1, -2, 3))).as("maxPathSum of [1, -2, 3]").isEqualTo(4);
    }

    @Test
    void skipsNegativeChildren() {
        assertThat(Solution.maxPathSum(tree(2, -1, -2))).as("maxPathSum of [2, -1, -2]").isEqualTo(2);
    }

    @Test
    void negativeRootCanBridgeTwoPositiveChildren() {
        assertThat(Solution.maxPathSum(tree(-1, 5, 5))).as("maxPathSum of [-1, 5, 5]").isEqualTo(9);
    }

    @Test
    void pathMayEndAtAnInternalNode() {
        assertThat(Solution.maxPathSum(tree(5, 4, null, -10, -10))).as("maxPathSum of [5, 4, null, -10, -10]").isEqualTo(9);
    }

    @Test
    void cannotRevisitANodeToUseItTwice() {
        assertThat(Solution.maxPathSum(tree(1, 2, 3, 4, 5, 6, 7))).as("maxPathSum of a complete 7-node tree").isEqualTo(18);
    }

    @Test
    void handlesLargeValues() {
        assertThat(Solution.maxPathSum(tree(500_000_000, 500_000_000, 500_000_000))).as("maxPathSum of three 500 million nodes").isEqualTo(1_500_000_000);
    }

    @Test
    void sumsAWholeDeepChain() {
        assertThat(Solution.maxPathSum(leftChain(1000))).as("maxPathSum of a 1000-node left chain").isEqualTo(500_500);
    }

    @Test
    void sumsAWholeDeepRightChain() {
        assertThat(Solution.maxPathSum(rightChain(1000))).as("maxPathSum of a 1000-node right chain").isEqualTo(500_500);
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
