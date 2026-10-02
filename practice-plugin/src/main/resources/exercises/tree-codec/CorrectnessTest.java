package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void roundTripsASingleNode() {
        assertThat(shape(roundTrip(tree(42)))).as("round trip of [42]").isEqualTo("[42]");
    }

    @Test
    void roundTripsZeroAndDuplicates() {
        assertThat(shape(roundTrip(tree(0, 0, 0, 0, null, null, 0)))).as("round trip of zeros").isEqualTo("[0, 0, 0, 0, null, null, 0]");
    }

    @Test
    void roundTripsExtremeValues() {
        assertThat(shape(roundTrip(tree(Integer.MIN_VALUE, Integer.MAX_VALUE, -1)))).as("round trip with extreme values")
                .isEqualTo("[" + Integer.MIN_VALUE + ", " + Integer.MAX_VALUE + ", -1]");
    }

    @Test
    void roundTripsATreeThatIsNotASearchTree() {
        assertThat(shape(roundTrip(tree(5, 9, 1, 8, 2)))).as("round trip of [5, 9, 1, 8, 2]").isEqualTo("[5, 9, 1, 8, 2]");
    }

    @Test
    void preservesGapsInTheMiddleOfLevels() {
        assertThat(shape(roundTrip(tree(1, null, 2, 3, null, 4)))).as("round trip of [1, null, 2, 3, null, 4]").isEqualTo("[1, null, 2, 3, null, 4]");
    }

    @Test
    void distinguishesLeftFromRightChildren() {
        assertThat(shape(roundTrip(tree(1, 2)))).as("round trip of a left child").isEqualTo("[1, 2]");
        assertThat(shape(roundTrip(tree(1, null, 2)))).as("round trip of a right child").isEqualTo("[1, null, 2]");
    }

    @Test
    void roundTripsACompleteTree() {
        assertThat(shape(roundTrip(complete(63)))).as("round trip of a complete 63-node tree").isEqualTo(shape(complete(63)));
    }

    @Test
    void roundTripsDeepLeftChain() {
        assertThat(shape(roundTrip(leftChain(800)))).as("round trip of an 800-node left chain").isEqualTo(shape(leftChain(800)));
    }

    @Test
    void roundTripsDeepRightChain() {
        assertThat(shape(roundTrip(rightChain(800)))).as("round trip of an 800-node right chain").isEqualTo(shape(rightChain(800)));
    }

    @Test
    void buildsNewNodes() {
        Solution.TreeNode original = tree(1, 2, 3);
        Solution.TreeNode rebuilt = roundTrip(original);
        assertThat(rebuilt).as("rebuilt root").isNotSameAs(original);
        assertThat(rebuilt.left).as("rebuilt left child").isNotSameAs(original.left);
    }

    @Test
    void doesNotModifyTheTreeWhileSerializing() {
        Solution.TreeNode original = tree(3, 1, null, 2);
        Solution.serialize(original);
        assertThat(shape(original)).as("tree after serialize").isEqualTo("[3, 1, null, 2]");
    }

    @Test
    void serializesEqualTreesToEqualText() {
        assertThat(Solution.serialize(tree(4, 5, 6))).as("serialize of two equal trees").isEqualTo(Solution.serialize(tree(4, 5, 6)));
    }

    @Test
    void keepsNoStateBetweenCalls() {
        String first = Solution.serialize(tree(1, 2, 3));
        String second = Solution.serialize(tree(9));
        assertThat(shape(Solution.deserialize(first))).as("first string after other calls").isEqualTo("[1, 2, 3]");
        assertThat(shape(Solution.deserialize(second))).as("second string").isEqualTo("[9]");
        assertThat(shape(Solution.deserialize(first))).as("first string decoded twice").isEqualTo("[1, 2, 3]");
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

    private static Solution.TreeNode roundTrip(Solution.TreeNode root) {
        String data = Solution.serialize(root);
        assertThat(data).as("serialize result").isNotNull();
        return Solution.deserialize(data);
    }
}
