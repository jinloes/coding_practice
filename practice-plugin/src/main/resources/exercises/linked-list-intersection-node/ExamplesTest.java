package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void findsSharedTail() { Solution.Node shared = list(8,9); assertThat(Solution.firstIntersection(append(list(1,2), shared), append(list(3), shared))).isSameAs(shared); }
    @Test
    void ignoresEqualValuesInSeparateNodes() { assertThat(Solution.firstIntersection(list(1,2), list(1,2))).isNull(); }
    @Test
    void returnsSharedHead() { Solution.Node head = list(4,5); assertThat(Solution.firstIntersection(head, head)).isSameAs(head); }
    private static Solution.Node list(int... values) { Solution.Node head = null; for (int i = values.length - 1; i >= 0; i--) head = new Solution.Node(values[i], head); return head; }
    private static Solution.Node append(Solution.Node prefix, Solution.Node tail) { if (prefix == null) return tail; Solution.Node p = prefix; while (p.next != null) p = p.next; p.next = tail; return prefix; }
}
