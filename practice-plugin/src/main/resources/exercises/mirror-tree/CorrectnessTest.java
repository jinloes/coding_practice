package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void singleNodeIsUnchanged() {
        Solution.TreeNode node = new Solution.TreeNode(-3);
        assertThat(Solution.mirror(node)).as("mirror of a single node").isSameAs(node);
        assertThat(shape(node)).as("single node after mirroring").isEqualTo("[-3]");
    }

    @Test
    void returnsTheOriginalRootAndSwapsItsChildren() {
        Solution.TreeNode left = new Solution.TreeNode(2);
        Solution.TreeNode right = new Solution.TreeNode(3);
        Solution.TreeNode root = new Solution.TreeNode(1, left, right);
        assertThat(Solution.mirror(root)).as("mirror must return the node it was given").isSameAs(root);
        assertThat(root.left).as("new left child").isSameAs(right);
        assertThat(root.right).as("new right child").isSameAs(left);
    }

    @Test
    void swapsEveryLevelNotJustTheRoot() {
        Solution.TreeNode root = tree(1, 2, 3, 4, 5, 6, 7);
        Solution.mirror(root);
        assertThat(shape(root)).as("mirror of a complete 7-node tree").isEqualTo("[1, 3, 2, 7, 6, 5, 4]");
    }

    @Test
    void mirroringTwiceRestoresTheTree() {
        Solution.TreeNode root = tree(8, -1, 4, null, 6, 0, null, 9);
        String before = shape(root);
        Solution.mirror(Solution.mirror(root));
        assertThat(shape(root)).as("tree after two mirrors").isEqualTo(before);
    }

    @Test
    void handlesOneSidedTrees() {
        assertThat(shape(Solution.mirror(tree(1, null, 2, null, 3)))).as("mirror of a right chain").isEqualTo("[1, 2, null, 3]");
    }

    @Test
    void preservesNegativeAndDuplicateValues() {
        assertThat(shape(Solution.mirror(tree(-1, -1, 0, 0, -1)))).as("mirror of [-1, -1, 0, 0, -1]").isEqualTo("[-1, 0, -1, null, null, -1, 0]");
    }

    @Test
    void convertsADeepLeftChainToARightChain() {
        Solution.TreeNode root = Solution.mirror(leftChain(1000));
        int depth = 0;
        for (Solution.TreeNode node = root; node != null; node = node.right) {
            depth++;
            assertThat(node.left).as("node " + node.value + " must have no left child after mirroring").isNull();
        }
        assertThat(depth).as("length of the resulting right chain").isEqualTo(1000);
    }

    @Test
    void reusesTheOriginalNodes() {
        Solution.TreeNode root = complete(15);
        java.util.Set<Solution.TreeNode> original = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
        collect(root, original);
        Solution.TreeNode result = Solution.mirror(root);
        java.util.Set<Solution.TreeNode> after = java.util.Collections.newSetFromMap(new java.util.IdentityHashMap<>());
        collect(result, after);
        assertThat(after).as("mirror must reuse exactly the supplied nodes").isEqualTo(original);
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

    private static void collect(Solution.TreeNode node, java.util.Set<Solution.TreeNode> into) {
        if (node != null) {
            into.add(node);
            collect(node.left, into);
            collect(node.right, into);
        }
    }
}
