package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void keepsAlreadyUniqueList() { assertThat(values(Solution.removeDuplicates(list(1,2,3)))).containsExactly(1,2,3); }
    @Test
    void removesDuplicateTail() { assertThat(values(Solution.removeDuplicates(list(1,2,3,1)))).containsExactly(1,2,3); }
    @Test
    void handlesNegativeAndZeroValues() { assertThat(values(Solution.removeDuplicates(list(0,-1,0,-1,2)))).containsExactly(0,-1,2); }
    @Test
    void preservesFirstNodeIdentity() { Solution.Node head = list(5,6,5); assertThat(Solution.removeDuplicates(head)).isSameAs(head); }
    @Test
    void handlesLongPattern() { Solution.Node head = null; for (int i = 999; i >= 0; i--) head = new Solution.Node(i % 37, head); int[] values = values(Solution.removeDuplicates(head)); assertThat(values).hasSize(37); assertThat(values[0]).isEqualTo(0); assertThat(values[36]).isEqualTo(36); }

    private static Solution.Node list(int... values) { Solution.Node head = null; for (int i = values.length - 1; i >= 0; i--) head = new Solution.Node(values[i], head); return head; }
    private static int[] values(Solution.Node head) { int n = 0; for (Solution.Node p = head; p != null; p = p.next) n++; int[] out = new int[n]; for (int i = 0; head != null; head = head.next) out[i++] = head.value; return out; }
}
