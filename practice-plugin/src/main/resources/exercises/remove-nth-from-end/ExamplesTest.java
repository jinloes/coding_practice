package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void removesAMiddleNode() {
        Solution.Node head = chain(1, 2, 3, 4, 5);
        assertThat(values(Solution.removeFromEnd(head, 2)))
                .as("removeFromEnd([1, 2, 3, 4, 5], 2)").containsExactly(1, 2, 3, 5);
    }

    @Test
    void removingTheOnlyNodeReturnsNull() {
        assertThat(Solution.removeFromEnd(chain(7), 1)).as("removeFromEnd([7], 1)").isNull();
    }

    @Test
    void removingTheHeadReturnsTheSecondNode() {
        assertThat(values(Solution.removeFromEnd(chain(1, 2), 2)))
                .as("removeFromEnd([1, 2], 2)").containsExactly(2);
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
