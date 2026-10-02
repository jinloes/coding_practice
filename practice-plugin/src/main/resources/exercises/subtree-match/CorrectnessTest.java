package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void nullSubAppearsInEveryTree() {
        assertThat(Solution.hasSubtree(tree(1, 2), null)).as("null inside [1, 2]").isTrue();
        assertThat(Solution.hasSubtree(null, null)).as("null inside null").isTrue();
    }

    @Test
    void nonNullSubCannotAppearInAnEmptyTree() {
        assertThat(Solution.hasSubtree(null, tree(1))).as("[1] inside null").isFalse();
    }

    @Test
    void wholeTreeIsASubtreeOfItself() {
        assertThat(Solution.hasSubtree(tree(3, 4, 5, 1, 2), tree(3, 4, 5, 1, 2))).as("tree inside an equal tree").isTrue();
    }

    @Test
    void findsALeafDeepInTheTree() {
        assertThat(Solution.hasSubtree(tree(1, 2, 3, 4, 5, 6, 7), tree(7))).as("[7] inside a complete 7-node tree").isTrue();
    }

    @Test
    void rejectsAMissingValue() {
        assertThat(Solution.hasSubtree(tree(1, 2, 3), tree(4))).as("[4] inside [1, 2, 3]").isFalse();
    }

    @Test
    void requiresTheSameShapeNotJustTheSameValues() {
        assertThat(Solution.hasSubtree(tree(1, 2, 3), tree(1, 2))).as("[1, 2] inside [1, 2, 3]").isFalse();
        assertThat(Solution.hasSubtree(tree(5, 2), tree(2, null, 3))).as("a node with a missing child").isFalse();
    }

    @Test
    void distinguishesLeftChildFromRightChild() {
        assertThat(Solution.hasSubtree(tree(9, 2, null), tree(9, null, 2))).as("[9, null, 2] inside [9, 2]").isFalse();
    }

    @Test
    void keepsSearchingAfterAPartialMatch() {
        assertThat(Solution.hasSubtree(tree(7, 3, 3, null, 9, 5, 6), tree(3, 5, 6))).as("second [3, 5, 6] after a failed first 3").isTrue();
    }

    @Test
    void rejectsASubLargerThanTheRoot() {
        assertThat(Solution.hasSubtree(tree(2, 1), tree(2, 1, 3, 4))).as("larger sub").isFalse();
    }

    @Test
    void handlesNegativeValues() {
        assertThat(Solution.hasSubtree(tree(-1, -2, -3), tree(-3))).as("[-3] inside [-1, -2, -3]").isTrue();
        assertThat(Solution.hasSubtree(tree(-1, -2, -3), tree(3))).as("[3] inside [-1, -2, -3]").isFalse();
    }

    @Test
    void findsAChainSuffixInsideADeepChain() {
        Solution.TreeNode sub = null;
        for (int i = 1000; i >= 600; i--) {
            sub = new Solution.TreeNode(i, sub, null);
        }
        assertThat(Solution.hasSubtree(leftChain(1000), sub)).as("values 600 to 1000 inside a 1000-node chain").isTrue();
    }

    @Test
    void rejectsAChainPrefixInsideADeepChain() {
        assertThat(Solution.hasSubtree(leftChain(1000), leftChain(500))).as("values 1 to 500 inside a 1000-node chain").isFalse();
    }

    @Test
    void doesNotModifyEitherTree() {
        Solution.TreeNode root = tree(1, 2, 3);
        Solution.TreeNode sub = tree(2);
        Solution.hasSubtree(root, sub);
        assertThat(shape(root)).as("root after the call").isEqualTo("[1, 2, 3]");
        assertThat(shape(sub)).as("sub after the call").isEqualTo("[2]");
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
