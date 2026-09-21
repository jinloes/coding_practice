package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void interleavesTwoSortedLists() {
        Solution.Node first = list(1, 2, 4);
        Solution.Node second = list(1, 3, 4);
        assertThat(values(Solution.merge(first, second)))
                .as("merge([1, 2, 4], [1, 3, 4])")
                .containsExactly(1, 1, 2, 3, 4, 4);
    }

    @Test
    void returnsTheOnlyNodeWhenOneListIsEmpty() {
        Solution.Node only = new Solution.Node(0, null);
        assertThat(Solution.merge(null, only))
                .as("merge(null, [0]) must return that same node")
                .isSameAs(only);
    }

    @Test
    void mergesTwoEmptyLists() {
        assertThat(Solution.merge(null, null)).as("merge(null, null) must return null").isNull();
    }

    private static Solution.Node list(int... values) {
        Solution.Node head = null;
        for (int index = values.length - 1; index >= 0; index--) {
            head = new Solution.Node(values[index], head);
        }
        return head;
    }

    private static List<Integer> values(Solution.Node head) {
        List<Integer> values = new ArrayList<>();
        for (Solution.Node node = head; node != null && values.size() <= 100; node = node.next) {
            values.add(node.value);
        }
        return values;
    }
}
