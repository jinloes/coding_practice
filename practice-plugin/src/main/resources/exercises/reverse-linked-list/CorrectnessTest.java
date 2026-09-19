package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.IdentityHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesNullAndSingletonInputs() {
        assertThat(Solution.reverse(null)).isNull();
        Solution.Node node = new Solution.Node(-4, null);
        assertThat(Solution.reverse(node)).isSameAs(node);
        assertThat(node.value).isEqualTo(-4);
        assertThat(node.next).isNull();
    }

    @Test
    void reversesTwoNodesAndReusesBoth() {
        Solution.Node first = new Solution.Node(1, null);
        Solution.Node second = new Solution.Node(2, first);
        Solution.Node result = Solution.reverse(second);
        assertThat(result).isSameAs(first);
        assertThat(result.next).isSameAs(second);
        assertThat(second.next).isNull();
    }

    @Test
    void reversesNegativeAndDuplicateValues() {
        Solution.Node head = chain(4, -2, 4, 0, -2);
        Solution.Node result = Solution.reverse(head);
        assertThat(values(result)).containsExactly(-2, 0, 4, -2, 4);
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
        assertThat(original).containsKeys(result, result.next, result.next.next);
        assertThat(result.next.next.next).isNull();
    }

    @Test
    void preservesNodeValues() {
        Solution.Node head = chain(Integer.MIN_VALUE, 0, Integer.MAX_VALUE);
        assertThat(values(Solution.reverse(head)))
                .containsExactly(Integer.MAX_VALUE, 0, Integer.MIN_VALUE);
    }

    @Test
    void reversesAListThatRequiresSeveralLinkChanges() {
        Solution.Node result = Solution.reverse(chain(1, 2, 3, 4, 5, 6, 7));
        assertThat(values(result)).containsExactly(7, 6, 5, 4, 3, 2, 1);
    }

    @Test
    void leavesTheReversedTailTerminated() {
        Solution.Node result = Solution.reverse(chain(9, 8, 7, 6));
        Solution.Node tail = result;
        while (tail.next != null) {
            tail = tail.next;
        }
        assertThat(tail.value).isEqualTo(9);
        assertThat(tail.next).isNull();
    }

    @Test
    void doesNotAllocateReplacementNodes() {
        Solution.Node head = chain(3, 1, 4, 1, 5);
        Map<Solution.Node, Boolean> original = new IdentityHashMap<>();
        for (Solution.Node node = head; node != null; node = node.next) {
            original.put(node, true);
        }
        for (Solution.Node node = Solution.reverse(head); node != null; node = node.next) {
            assertThat(original).containsKey(node);
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
