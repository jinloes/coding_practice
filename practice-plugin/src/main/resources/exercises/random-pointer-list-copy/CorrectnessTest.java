package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void singleNodeWithoutRandom() {
        assertDeepCopy(new int[]{9}, new int[]{-1});
    }

    @Test
    void everyRandomPointsAtTheHead() {
        assertDeepCopy(new int[]{1, 2, 3, 4}, new int[]{0, 0, 0, 0});
    }

    @Test
    void randomPointersGoBackward() {
        assertDeepCopy(new int[]{4, 5, 6, 7, 8}, new int[]{-1, 0, 1, 2, 3});
    }

    @Test
    void randomPointersGoForward() {
        assertDeepCopy(new int[]{4, 5, 6, 7, 8}, new int[]{4, 3, 2, 1, -1});
    }

    @Test
    void duplicateValuesAreCopiedByPosition() {
        assertDeepCopy(new int[]{2, 2, 2, 2}, new int[]{3, 2, -1, 0});
    }

    @Test
    void noRandomPointersAtAll() {
        assertDeepCopy(new int[]{-1, 0, 1}, new int[]{-1, -1, -1});
    }

    @Test
    void largeSeededList() {
        Random random = new Random(7);
        int n = 3000;
        int[] values = new int[n];
        int[] randoms = new int[n];
        for (int i = 0; i < n; i++) {
            values[i] = random.nextInt(100) - 50;
            randoms[i] = random.nextInt(n + 1) - 1;
        }
        assertDeepCopy(values, randoms);
    }

    private static Solution.Node build(int[] values, int[] randoms) {
        Solution.Node[] nodes = new Solution.Node[values.length];
        for (int i = 0; i < values.length; i++) {
            nodes[i] = new Solution.Node(values[i]);
            if (i > 0) {
                nodes[i - 1].next = nodes[i];
            }
        }
        for (int i = 0; i < values.length; i++) {
            nodes[i].random = randoms[i] < 0 ? null : nodes[randoms[i]];
        }
        return values.length == 0 ? null : nodes[0];
    }

    private static void assertDeepCopy(int[] values, int[] randoms) {
        Solution.Node head = build(values, randoms);
        Map<Solution.Node, Integer> originalIndex = new IdentityHashMap<>();
        for (Solution.Node n = head; n != null; n = n.next) {
            originalIndex.put(n, originalIndex.size());
        }
        Solution.Node copy = Solution.copyList(head);
        assertThat(copy).as("copyList of %s", Arrays.toString(values)).isNotNull();
        Map<Solution.Node, Integer> copyIndex = new IdentityHashMap<>();
        List<Solution.Node> copies = new ArrayList<>();
        for (Solution.Node n = copy; n != null; n = n.next) {
            assertThat(originalIndex).as("copy node %s must not be an original node", n.value).doesNotContainKey(n);
            assertThat(copyIndex).as("copy list must not repeat a node").doesNotContainKey(n);
            copyIndex.put(n, copies.size());
            copies.add(n);
        }
        assertThat(copies).as("copy length").hasSize(values.length);
        Solution.Node original = head;
        for (int i = 0; i < values.length; i++, original = original.next) {
            Solution.Node node = copies.get(i);
            assertThat(node.value).as("value at position %d", i).isEqualTo(values[i]);
            if (randoms[i] < 0) {
                assertThat(node.random).as("random pointer at position %d", i).isNull();
            } else {
                assertThat(copyIndex.get(node.random)).as("random target index at position %d", i)
                        .isEqualTo(randoms[i]);
            }
            assertThat(originalIndex.get(original)).as("original list must stay intact").isEqualTo(i);
            if (randoms[i] >= 0) {
                assertThat(originalIndex.get(original.random)).as("original random must stay intact")
                        .isEqualTo(randoms[i]);
            }
        }
    }
}
