package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorrectnessTest {
    @Test
    void handlesNullHead() { assertThat(Solution.kthFromEnd(null, 1)).isNull(); }
    @Test
    void returnsHeadWhenKEqualsLength() { Solution.Node head = list(5,6,7); assertThat(Solution.kthFromEnd(head, 3)).isSameAs(head); }
    @Test
    void preservesIdentityWithDuplicateValues() { Solution.Node head = list(1,9,9,2); assertThat(Solution.kthFromEnd(head, 2)).isSameAs(head.next.next); }
    @Test
    void rejectsNonPositiveK() { assertThatThrownBy(() -> Solution.kthFromEnd(list(1), 0)).isInstanceOf(IllegalArgumentException.class); }
    @Test
    void handlesLongList() { Solution.Node head = null; Solution.Node expected = null; for (int i = 1000; i >= 1; i--) { head = new Solution.Node(i, head); if (i == 901) expected = head; } assertThat(Solution.kthFromEnd(head, 100)).isSameAs(expected); }
    private static Solution.Node list(int... values) { Solution.Node head = null; for (int i = values.length - 1; i >= 0; i--) head = new Solution.Node(values[i], head); return head; }
}
