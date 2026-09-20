package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void reversesThreeNodes() {
        Solution.Node one = new Solution.Node(1, new Solution.Node(2, new Solution.Node(3, null)));
        Solution.Node result = Solution.reverse(one);
        assertThat(values(result)).as("reverse of [1, 2, 3]").containsExactly(3, 2, 1);
        assertThat(result.next.next.next)
                .as("reverse of [1, 2, 3] must terminate the new tail").isNull();
    }

    @Test
    void preservesASingleNode() {
        Solution.Node node = new Solution.Node(8, null);
        assertThat(Solution.reverse(node))
                .as("reverse of the single node [8] must return that same node").isSameAs(node);
    }

    @Test
    void reversesAnEmptyList() {
        assertThat(Solution.reverse(null)).as("reverse(null) must return null").isNull();
    }

    private static List<Integer> values(Solution.Node head) {
        List<Integer> values = new ArrayList<>();
        for (Solution.Node node = head; node != null; node = node.next) {
            values.add(node.value);
        }
        return values;
    }
}
