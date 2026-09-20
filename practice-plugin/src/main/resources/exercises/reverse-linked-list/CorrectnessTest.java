package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.IdentityHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesNullAndSingletonInputs() {
        assertThat(Solution.reverse(null)).as("reverse(null) must return null").isNull();
        Solution.Node node = new Solution.Node(-4, null);
        assertThat(Solution.reverse(node)).as("reverse of a single node must return that same node").isSameAs(node);
        assertThat(node.value).as("reverse must not change the single node value").isEqualTo(-4);
        assertThat(node.next).as("the single node must stay terminated").isNull();
    }

    @Test
    void reversesTwoNodesAndReusesBoth() {
        Solution.Node first = new Solution.Node(1, null);
        Solution.Node second = new Solution.Node(2, first);
        Solution.Node result = Solution.reverse(second);
        assertThat(values(result)).as("reverse of [2, 1] returned %s", java.util.Arrays.toString(values(result))).containsExactly(1, 2);
        assertThat(result).as("reverse of [2, 1] must reuse the original tail node as the new head").isSameAs(first);
        assertThat(result.next).as("reverse of [2, 1] must link the new head to the original head node").isSameAs(second);
        assertThat(second.next).as("reverse of [2, 1] must terminate the new tail").isNull();
    }

    @Test
    void reversesNegativeAndDuplicateValues() {
        Solution.Node head = chain(4, -2, 4, 0, -2);
        Solution.Node result = Solution.reverse(head);
        assertThat(values(result)).as("reverse of [4, -2, 4, 0, -2]").containsExactly(-2, 0, 4, -2, 4);
    }

    @Test
    void preservesEveryNodeIdentity() {
        Solution.Node first = new Solution.Node(10, null);
        Solution.Node second = new Solution.Node(20, null);
        Solution.Node third = new Solution.Node(30, null);
        first.next = second;
        second.next = third;
        Map<Solution.Node, Boolean> original = new IdentityHashMap<>();
        original.put(first, true);
        original.put(second, true);
        original.put(third, true);
        Solution.Node result = Solution.reverse(first);
        assertThat(values(result)).as("reverse of [10, 20, 30]").containsExactly(30, 20, 10);
        assertThat(original).as("reverse of [10, 20, 30] must reuse the original nodes, not allocate new ones")
                .containsKeys(result, result.next, result.next.next);
        assertThat(result.next.next.next).as("reverse of [10, 20, 30] must terminate the new tail").isNull();
    }

    @Test
    void preservesNodeValues() {
        Solution.Node head = chain(Integer.MIN_VALUE, 0, Integer.MAX_VALUE);
        assertThat(values(Solution.reverse(head)))
                .as("reverse of [MIN_VALUE, 0, MAX_VALUE]")
                .containsExactly(Integer.MAX_VALUE, 0, Integer.MIN_VALUE);
    }

    @Test
    void reversesAListThatRequiresSeveralLinkChanges() {
        Solution.Node result = Solution.reverse(chain(1, 2, 3, 4, 5, 6, 7));
        assertThat(values(result)).as("reverse of [1, 2, 3, 4, 5, 6, 7]").containsExactly(7, 6, 5, 4, 3, 2, 1);
    }

    @Test
    void leavesTheReversedTailTerminated() {
        Solution.Node result = Solution.reverse(chain(9, 8, 7, 6));
        Solution.Node tail = result;
        while (tail.next != null) {
            tail = tail.next;
        }
        assertThat(values(result)).as("reverse of [9, 8, 7, 6]").containsExactly(6, 7, 8, 9);
        assertThat(tail.value).as("reverse of [9, 8, 7, 6] must end at the original head value").isEqualTo(9);
        assertThat(tail.next).as("reverse of [9, 8, 7, 6] must terminate the new tail").isNull();
    }

    @Test
    void doesNotAllocateReplacementNodes() {
        Solution.Node head = chain(3, 1, 4, 1, 5);
        Map<Solution.Node, Boolean> original = new IdentityHashMap<>();
        for (Solution.Node node = head; node != null; node = node.next) {
            original.put(node, true);
        }
        for (Solution.Node node = Solution.reverse(head); node != null; node = node.next) {
            assertThat(original)
                    .as("reverse of [3, 1, 4, 1, 5] returned a node with value %d that was not in the input list", node.value)
                    .containsKey(node);
        }
    }

    private static Solution.Node chain(int... values) {
        Solution.Node head = null;
        for (int i = values.length - 1; i >= 0; i--) {
            head = new Solution.Node(values[i], head);
        }
        return head;
    }

    private static int[] values(Solution.Node head) {
        java.util.ArrayList<Integer> values = new java.util.ArrayList<>();
        for (Solution.Node node = head; node != null; node = node.next) {
            values.add(node.value);
        }
        return values.stream().mapToInt(Integer::intValue).toArray();
    }
}
