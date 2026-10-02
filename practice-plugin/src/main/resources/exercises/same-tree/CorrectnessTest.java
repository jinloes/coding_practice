package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void twoEmptyTreesMatch() {
        assertThat(Solution.isSameTree(null, null)).as("null vs null").isTrue();
    }

    @Test
    void emptyDoesNotMatchNonEmpty() {
        assertThat(Solution.isSameTree(null, tree(0))).as("null vs [0]").isFalse();
        assertThat(Solution.isSameTree(tree(0), null)).as("[0] vs null").isFalse();
    }

    @Test
    void singleNodesCompareByValue() {
        assertThat(Solution.isSameTree(tree(-5), tree(-5))).as("[-5] vs [-5]").isTrue();
        assertThat(Solution.isSameTree(tree(5), tree(-5))).as("[5] vs [-5]").isFalse();
    }

    @Test
    void detectsADifferenceInTheRightSubtree() {
        assertThat(Solution.isSameTree(tree(1, 2, 3, 4, 5), tree(1, 2, 3, 4, 6))).as("[1, 2, 3, 4, 5] vs [1, 2, 3, 4, 6]").isFalse();
    }

    @Test
    void detectsADifferenceInTheLeftSubtree() {
        assertThat(Solution.isSameTree(tree(1, 2, 3, 4, 5), tree(1, 2, 3, 0, 5))).as("[1, 2, 3, 4, 5] vs [1, 2, 3, 0, 5]").isFalse();
    }

    @Test
    void rejectsAMirroredTree() {
        assertThat(Solution.isSameTree(tree(1, 2, 3), tree(1, 3, 2))).as("[1, 2, 3] vs [1, 3, 2]").isFalse();
    }

    @Test
    void rejectsATreeWithAnExtraDescendant() {
        assertThat(Solution.isSameTree(tree(1, 2), tree(1, 2, null, 3))).as("[1, 2] vs [1, 2, null, 3]").isFalse();
    }

    @Test
    void acceptsEqualDeepChains() {
        assertThat(Solution.isSameTree(leftChain(1000), leftChain(1000))).as("two 1000-node left chains").isTrue();
    }

    @Test
    void rejectsChainsThatDifferOnlyAtTheBottom() {
        Solution.TreeNode other = leftChain(1000);
        Solution.TreeNode bottom = other;
        while (bottom.left != null) {
            bottom = bottom.left;
        }
        bottom.value = -1;
        assertThat(Solution.isSameTree(leftChain(1000), other)).as("chains differing in the deepest value").isFalse();
    }

    @Test
    void acceptsDistinctInstancesWithEqualContent() {
        assertThat(Solution.isSameTree(complete(31), complete(31))).as("two separate complete 31-node trees").isTrue();
    }

    @Test
    void doesNotModifyEitherTree() {
        Solution.TreeNode a = tree(1, 2, 3, 4);
        Solution.TreeNode b = tree(1, 2, 3, 5);
        Solution.isSameTree(a, b);
        assertThat(shape(a)).as("first tree after the call").isEqualTo("[1, 2, 3, 4]");
        assertThat(shape(b)).as("second tree after the call").isEqualTo("[1, 2, 3, 5]");
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

    private static String shape(Solution.TreeNode root) {
        if (root == null) {
            return "[]";
        }
        java.util.List<String> out = new java.util.ArrayList<>();
        java.util.LinkedList<Solution.TreeNode> queue = new java.util.LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty() && out.size() < 100_000) {
            Solution.TreeNode node = queue.poll();
            if (node == null) {
                out.add("null");
            } else {
                out.add(String.valueOf(node.value));
                queue.add(node.left);
                queue.add(node.right);
            }
        }
        while (out.get(out.size() - 1).equals("null")) {
            out.remove(out.size() - 1);
        }
        return "[" + String.join(", ", out) + "]";
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
