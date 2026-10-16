package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorrectnessTest {
    @Test
    void emptyListReturnsNull() {
        assertThat(Solution.reverseInGroups(null, 3)).isNull();
    }

    @Test
    void rejectsGroupSizeBelowOne() {
        assertThatThrownBy(() -> Solution.reverseInGroups(new Solution.ListNode(1), 0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Solution.reverseInGroups(new Solution.ListNode(1), -2))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void groupLargerThanListLeavesItAlone() {
        assertReversed(3, 5, 1, 2, 3);
    }

    @Test
    void groupEqualToLengthReversesWholeList() {
        assertReversed(4, 4, 4, 3, 2, 1);
    }

    @Test
    void tailOfSizeKMinusOneIsLeftAlone() {
        assertReversed(8, 3, 3, 2, 1, 6, 5, 4, 7, 8);
    }

    @Test
    void singleNode() {
        assertReversed(1, 2, 1);
        assertReversed(1, 1, 1);
    }

    @Test
    void longListWithTail() {
        int n = 10_001;
        int k = 4;
        int[] expected = new int[n];
        int filled = 0;
        for (int start = 1; start + k - 1 <= n; start += k) {
            for (int v = start + k - 1; v >= start; v--) {
                expected[filled++] = v;
            }
        }
        for (int v = filled + 1; v <= n; v++) {
            expected[filled++] = v;
        }
        assertReversed(n, k, expected);
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
