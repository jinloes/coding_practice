package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void removesLaterDuplicates() { assertThat(values(Solution.removeDuplicates(list(1,2,1,3,2)))).containsExactly(1,2,3); }
    @Test
    void collapsesAllEqualValues() { assertThat(values(Solution.removeDuplicates(list(4,4,4)))).containsExactly(4); }
    @Test
    void handlesEmptyList() { assertThat(Solution.removeDuplicates(null)).isNull(); }

    private static Solution.Node list(int... values) { Solution.Node head = null; for (int i = values.length - 1; i >= 0; i--) head = new Solution.Node(values[i], head); return head; }
    private static int[] values(Solution.Node head) { int n = 0; for (Solution.Node p = head; p != null; p = p.next) n++; int[] out = new int[n]; for (int i = 0; head != null; head = head.next) out[i++] = head.value; return out; }
}
