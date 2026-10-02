package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class CorrectnessTest {
    @Test
    void acceptsAnEmptyList() {
        reorder(null);
    }

    @Test
    void leavesTwoNodesInOrder() {
        Solution.Node head = chain(1, 2);
        reorder(head);
        assertThat(values(head)).as("reorder of [1, 2]").containsExactly(1, 2);
    }

    @Test
    void reordersThreeNodes() {
        Solution.Node head = chain(1, 2, 3);
        reorder(head);
        assertThat(values(head)).as("reorder of [1, 2, 3]").containsExactly(1, 3, 2);
    }

    @Test
    void reordersSixNodes() {
        Solution.Node head = chain(1, 2, 3, 4, 5, 6);
        reorder(head);
        assertThat(values(head)).as("reorder of [1, 2, 3, 4, 5, 6]").containsExactly(1, 6, 2, 5, 3, 4);
    }

    @Test
    void reordersSevenNodes() {
        Solution.Node head = chain(1, 2, 3, 4, 5, 6, 7);
        reorder(head);
        assertThat(values(head)).as("reorder of [1, 2, 3, 4, 5, 6, 7]").containsExactly(1, 7, 2, 6, 3, 5, 4);
    }

    @Test
    void handlesDuplicateAndNegativeValues() {
        Solution.Node head = chain(-1, 5, 5, -1, 0);
        reorder(head);
        assertThat(values(head)).as("reorder of [-1, 5, 5, -1, 0]").containsExactly(-1, 0, 5, -1, 5);
    }

    @Test
    void handlesExtremeValues() {
        Solution.Node head = chain(Integer.MIN_VALUE, 0, Integer.MAX_VALUE, -1);
        reorder(head);
        assertThat(values(head)).as("reorder of [MIN_VALUE, 0, MAX_VALUE, -1]")
                .containsExactly(Integer.MIN_VALUE, -1, 0, Integer.MAX_VALUE);
    }

    @Test
    void keepsTheSameHeadAndReusesEveryNode() {
        Solution.Node head = chain(1, 2, 3, 4, 5);
        Map<Solution.Node, Boolean> original = new IdentityHashMap<>();
        for (Solution.Node node = head; node != null; node = node.next) {
            original.put(node, true);
        }
        Solution.Node originalHead = head;
        reorder(head);
        assertThat(head).as("the head node must stay the head").isSameAs(originalHead);
        int count = 0;
        for (Solution.Node node = head; node != null && count < 100; node = node.next) {
            assertThat(original).as("reorder must reuse the supplied nodes, not allocate new ones").containsKey(node);
            count++;
        }
        assertThat(count).as("node count after reorder of five nodes").isEqualTo(5);
    }

    @Test
    void leavesTheNewTailTerminated() {
        Solution.Node head = chain(1, 2, 3, 4);
        reorder(head);
        List<Integer> walked = values(head);
        assertThat(walked).as("reorder of [1, 2, 3, 4] must end after four nodes").hasSize(4);
    }

    @Test
    void reordersEveryLengthUpToTwentyFive() {
        for (int length = 1; length <= 25; length++) {
            int[] numbers = new int[length];
            for (int i = 0; i < length; i++) {
                numbers[i] = i * 3 - 10;
            }
            Solution.Node head = chain(numbers);
            reorder(head);
            List<Integer> expected = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                expected.add(i % 2 == 0 ? numbers[i / 2] : numbers[length - 1 - i / 2]);
            }
            assertThat(values(head)).as("reorder of a list with %d nodes", length).containsExactlyElementsOf(expected);
        }
    }

    @Test
    void handlesALongList() {
        int length = 100001;
        int[] numbers = new int[length];
        for (int i = 0; i < length; i++) {
            numbers[i] = i;
        }
        Solution.Node head = chain(numbers);
        reorder(head);
        List<Integer> result = values(head);
        assertThat(result).as("node count after reorder of 100001 nodes").hasSize(length);
        assertThat(result.subList(0, 6)).as("first values after reorder of 0..100000")
                .containsExactly(0, 100000, 1, 99999, 2, 99998);
        assertThat(result.get(length - 1)).as("last value after reorder of 0..100000").isEqualTo(50000);
    }

    private static void reorder(Solution.Node head) {
        try {
            Solution.reorder(head);
        } catch (Throwable thrown) {
            fail("reorder threw %s".formatted(thrown), thrown);
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
