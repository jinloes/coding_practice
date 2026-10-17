package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesNulls() { assertThat(Solution.firstIntersection(null, null)).isNull(); assertThat(Solution.firstIntersection(null, chain(1))).isNull(); }
    @Test
    void findsSharedPrefixBeforeCommonCycle() { Solution.Node entry = cycle(7,8,9); Solution.Node shared = append(chain(4,5), entry); assertThat(Solution.firstIntersection(append(chain(1), shared), append(chain(2,3), shared))).isSameAs(shared); }
    @Test
    void returnsNullWhenOnlyOneListCycles() { assertThat(Solution.firstIntersection(cycle(1,2,3), chain(1,2,3))).isNull(); }
    @Test
    void findsAcyclicIntersectionWithUnequalLengths() { Solution.Node shared = chain(20,21); assertThat(Solution.firstIntersection(append(chain(1,2,3), shared), append(chain(4), shared))).isSameAs(shared); }
    @Test
    void ignoresEqualValuesInSeparateCycles() { assertThat(Solution.firstIntersection(cycle(1,2,3), cycle(1,2,3))).isNull(); }

    private record Fixture(Solution.Node first, Solution.Node second, Solution.Node expected) {}
    private static Fixture sharedCycleOnly() { Solution.Node e1 = new Solution.Node(10, null); Solution.Node e2 = new Solution.Node(11, null); Solution.Node e3 = new Solution.Node(12, null); e1.next = e2; e2.next = e3; e3.next = e1; return new Fixture(append(chain(1,2), e1), append(chain(3), e2), e1); }
    private static Solution.Node cycle(int... values) { Solution.Node head = chain(values); Solution.Node p = head; while (p.next != null) p = p.next; p.next = head; return head; }
    private static Solution.Node chain(int... values) { Solution.Node head = null; for (int i = values.length - 1; i >= 0; i--) head = new Solution.Node(values[i], head); return head; }
    private static Solution.Node append(Solution.Node prefix, Solution.Node tail) { if (prefix == null) return tail; Solution.Node p = prefix; while (p.next != null) p = p.next; p.next = tail; return prefix; }
}
