package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void reordersAnEvenLengthList() {
        Solution.Node head = chain(1, 2, 3, 4);
        Solution.reorder(head);
        assertThat(values(head)).as("reorder of [1, 2, 3, 4]").containsExactly(1, 4, 2, 3);
    }

    @Test
    void reordersAnOddLengthList() {
        Solution.Node head = chain(1, 2, 3, 4, 5);
        Solution.reorder(head);
        assertThat(values(head)).as("reorder of [1, 2, 3, 4, 5]").containsExactly(1, 5, 2, 4, 3);
    }

    @Test
    void leavesASingleNodeAlone() {
        Solution.Node head = chain(7);
        Solution.reorder(head);
        assertThat(values(head)).as("reorder of [7]").containsExactly(7);
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
        for (Solution.Node node = head; node != null && values.size() < 1000; node = node.next) {
            values.add(node.value);
        }
        return values;
    }
}
