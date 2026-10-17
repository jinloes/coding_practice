package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void returnsLastNodeForKOne() { assertThat(Solution.kthFromEnd(list(1,2,3,4), 1).value).isEqualTo(4); }
    @Test
    void returnsInteriorNode() { assertThat(Solution.kthFromEnd(list(1,2,3,4), 3).value).isEqualTo(2); }
    @Test
    void returnsNullWhenKTooLarge() { assertThat(Solution.kthFromEnd(list(1,2), 5)).isNull(); }
    private static Solution.Node list(int... values) { Solution.Node head = null; for (int i = values.length - 1; i >= 0; i--) head = new Solution.Node(values[i], head); return head; }
}
