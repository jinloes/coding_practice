package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExamplesTest {
    @Test
    void shortTailStaysInOrder() {
        assertReversed(5, 2, 2, 1, 4, 3, 5);
    }

    @Test
    void exactMultipleReversesEveryGroup() {
        assertReversed(6, 3, 3, 2, 1, 6, 5, 4);
    }

    @Test
    void groupSizeOneChangesNothing() {
        assertReversed(3, 1, 1, 2, 3);
    }

    private static void assertReversed(int length, int k, int... expectedLabels) {
        Solution.ListNode[] nodes = new Solution.ListNode[length];
        for (int i = 0; i < length; i++) {
            nodes[i] = new Solution.ListNode(i + 1);
            if (i > 0) {
                nodes[i - 1].next = nodes[i];
            }
        }
        Solution.ListNode result = Solution.reverseInGroups(length == 0 ? null : nodes[0], k);
        List<Solution.ListNode> actual = new ArrayList<>();
        for (Solution.ListNode n = result; n != null && actual.size() <= length; n = n.next) {
            actual.add(n);
        }
        Solution.ListNode[] expected = new Solution.ListNode[expectedLabels.length];
        for (int i = 0; i < expectedLabels.length; i++) {
            expected[i] = nodes[expectedLabels[i] - 1];
        }
        assertThat(actual).as("original nodes of 1..%d reordered in groups of %d", length, k)
                .containsExactly(expected);
        assertThat(actual.isEmpty() ? null : actual.get(actual.size() - 1).next)
                .as("the last node must end the list").isNull();
    }
}
