package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void nullListCopiesToNull() {
        assertThat(Solution.copyList(null)).isNull();
    }

    @Test
    void singleNodePointingAtItself() {
        assertDeepCopy(new int[]{5}, new int[]{0});
    }

    @Test
    void mixedRandomPointers() {
        assertDeepCopy(new int[]{3, 7, 1}, new int[]{1, -1, 0});
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
