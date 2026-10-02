package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void listsEachLevelFromLeftToRight() {
        assertThat(Solution.levelOrder(tree(1, 2, 3, 4, 5, 6, 7))).as("levelOrder of a complete 7-node tree")
                .containsExactly(List.of(1), List.of(2, 3), List.of(4, 5, 6, 7));
    }

    @Test
    void keepsOrderWhenSomeSlotsAreMissing() {
        assertThat(Solution.levelOrder(tree(1, 2, 3, null, 4, null, 5))).as("levelOrder of [1, 2, 3, null, 4, null, 5]")
                .containsExactly(List.of(1), List.of(2, 3), List.of(4, 5));
    }

    @Test
    void handlesARightLeaningTree() {
        assertThat(Solution.levelOrder(tree(1, null, 2, null, 3))).as("levelOrder of [1, null, 2, null, 3]")
                .containsExactly(List.of(1), List.of(2), List.of(3));
    }

    @Test
    void preservesNegativeAndDuplicateValues() {
        assertThat(Solution.levelOrder(tree(-1, -1, 0, 0, null, -1))).as("levelOrder of [-1, -1, 0, 0, null, -1]")
                .containsExactly(List.of(-1), List.of(-1, 0), List.of(0, -1));
    }

    @Test
    void handlesExtremeValues() {
        assertThat(Solution.levelOrder(tree(Integer.MAX_VALUE, Integer.MIN_VALUE, 0))).as("levelOrder with extreme values")
                .containsExactly(List.of(Integer.MAX_VALUE), List.of(Integer.MIN_VALUE, 0));
    }

    @Test
    void groupsACompleteTreeIntoGrowingLevels() {
        List<List<Integer>> levels = Solution.levelOrder(complete(15));
        assertThat(levels).as("number of levels in a complete 15-node tree").hasSize(4);
        assertThat(levels.get(3)).as("deepest level").containsExactly(8, 9, 10, 11, 12, 13, 14, 15);
    }

    @Test
    void putsEachNodeOfALeftChainOnItsOwnLevel() {
        List<List<Integer>> levels = Solution.levelOrder(leftChain(1000));
        assertThat(levels).as("number of levels in a 1000-node left chain").hasSize(1000);
        assertThat(levels.get(999)).as("deepest level").containsExactly(1000);
    }

    @Test
    void doesNotModifyTheTree() {
        Solution.TreeNode root = tree(1, 2, 3, 4, 5);
        Solution.levelOrder(root);
        assertThat(shape(root)).as("tree after the call").isEqualTo("[1, 2, 3, 4, 5]");
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
