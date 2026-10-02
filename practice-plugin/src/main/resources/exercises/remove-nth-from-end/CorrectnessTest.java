package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class CorrectnessTest {
    @Test
    void removesTheLastNode() {
        assertThat(values(remove(chain(1, 2, 3, 4), 1))).as("removeFromEnd([1, 2, 3, 4], 1)").containsExactly(1, 2, 3);
    }

    @Test
    void removesTheHeadOfALongerList() {
        assertThat(values(remove(chain(1, 2, 3, 4), 4))).as("removeFromEnd([1, 2, 3, 4], 4)").containsExactly(2, 3, 4);
    }

    @Test
    void removesTheSecondNode() {
        assertThat(values(remove(chain(1, 2, 3, 4), 3))).as("removeFromEnd([1, 2, 3, 4], 3)").containsExactly(1, 3, 4);
    }

    @Test
    void removesFromTwoNodeLists() {
        assertThat(values(remove(chain(8, 9), 1))).as("removeFromEnd([8, 9], 1)").containsExactly(8);
        assertThat(values(remove(chain(8, 9), 2))).as("removeFromEnd([8, 9], 2)").containsExactly(9);
    }

    @Test
    void removesByPositionNotByValue() {
        assertThat(values(remove(chain(4, 4, 4, 5), 2)))
                .as("removeFromEnd([4, 4, 4, 5], 2)").containsExactly(4, 4, 5);
        assertThat(values(remove(chain(6, 1, 6, 1, 6), 3)))
                .as("removeFromEnd([6, 1, 6, 1, 6], 3)").containsExactly(6, 1, 1, 6);
    }

    @Test
    void handlesNegativeAndExtremeValues() {
        assertThat(values(remove(chain(Integer.MIN_VALUE, -1, 0, Integer.MAX_VALUE), 3)))
                .as("removeFromEnd([MIN_VALUE, -1, 0, MAX_VALUE], 3)")
                .containsExactly(Integer.MIN_VALUE, 0, Integer.MAX_VALUE);
    }

    @Test
    void reusesTheRemainingNodes() {
        Solution.Node first = new Solution.Node(1, null);
        Solution.Node second = new Solution.Node(2, null);
        Solution.Node third = new Solution.Node(3, null);
        first.next = second;
        second.next = third;
        Solution.Node result = remove(first, 2);
        assertThat(result).as("removing a non-head node must keep the same head").isSameAs(first);
        assertThat(result.next).as("the node after the removed one must be linked in").isSameAs(third);
        assertThat(third.next).as("the tail must stay terminated").isNull();
    }

    @Test
    void removingTheHeadReturnsTheOldSecondNode() {
        Solution.Node first = new Solution.Node(1, null);
        Solution.Node second = new Solution.Node(2, null);
        first.next = second;
        assertThat(remove(first, 2)).as("removeFromEnd([1, 2], 2) must return the old second node").isSameAs(second);
    }

    @Test
    void handlesALongList() {
        int[] numbers = new int[100000];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = i;
        }
        List<Integer> result = values(remove(chain(numbers), 50000));
        assertThat(result).as("size after removing one node from a 100000-node list").hasSize(99999);
        assertThat(result.get(49999)).as("value before the removed position").isEqualTo(49999);
        assertThat(result.get(50000)).as("value after the removed position").isEqualTo(50001);
    }

    private static Solution.Node remove(Solution.Node head, int k) {
        try {
            return Solution.removeFromEnd(head, k);
        } catch (Throwable thrown) {
            return fail("removeFromEnd with k=%d threw %s".formatted(k, thrown), thrown);
        }
    }

    private static Solution.Node chain(int... values) {
        Solution.Node head = null;
        for (int i = values.length - 1; i >= 0; i--) {
            head = new Solution.Node(values[i], head);
        }
        return head;
    }

    private static List<Integer> values(Solution.Node head) {
        List<Integer> values = new ArrayList<>();
        for (Solution.Node node = head; node != null && values.size() <= 200000; node = node.next) {
            values.add(node.value);
        }
        return values;
    }
}
