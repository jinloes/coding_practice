package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesNullLists() { assertThat(Solution.firstIntersection(null, list(1))).isNull(); assertThat(Solution.firstIntersection(null, null)).isNull(); }
    @Test
    void findsIntersectionAfterUnequalPrefixes() { Solution.Node shared = list(30,31,32); assertThat(Solution.firstIntersection(append(list(1,2,3,4), shared), append(list(9), shared))).isSameAs(shared); }
    @Test
    void findsLateSharedNode() { Solution.Node shared = list(99); assertThat(Solution.firstIntersection(append(list(1,2), shared), append(list(3,4,5), shared))).isSameAs(shared); }
    @Test
    void leavesListsLinked() { Solution.Node shared = list(7); Solution.Node first = append(list(1), shared); Solution.Node second = append(list(2), shared); Solution.firstIntersection(first, second); assertThat(first.next).isSameAs(shared); assertThat(second.next).isSameAs(shared); }
    @Test
    void handlesLargeDisjointLists() { assertThat(Solution.firstIntersection(range(0,200), range(0,250))).isNull(); }
    private static Solution.Node range(int start, int count) { Solution.Node head = null; for (int i = count - 1; i >= 0; i--) head = new Solution.Node(start + i, head); return head; }
    private static Solution.Node list(int... values) { Solution.Node head = null; for (int i = values.length - 1; i >= 0; i--) head = new Solution.Node(values[i], head); return head; }
    private static Solution.Node append(Solution.Node prefix, Solution.Node tail) { if (prefix == null) return tail; Solution.Node p = prefix; while (p.next != null) p = p.next; p.next = tail; return prefix; }
}
