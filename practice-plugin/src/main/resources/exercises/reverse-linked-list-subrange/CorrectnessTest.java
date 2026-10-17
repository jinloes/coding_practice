package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesNullList() { assertThat(Solution.reverseBetween(null, 1, 1)).isNull(); }
    @Test
    void reversesPrefixOnly() { assertThat(values(Solution.reverseBetween(list(1,2,3,4), 1, 2))).containsExactly(2,1,3,4); }
    @Test
    void reversesSuffixOnly() { assertThat(values(Solution.reverseBetween(list(1,2,3,4), 3, 4))).containsExactly(1,2,4,3); }
    @Test
    void preservesNodeIdentityOutsideRange() { Solution.Node head = list(1,2,3,4); Solution.Node tail = head.next.next.next; Solution.Node result = Solution.reverseBetween(head, 2, 3); assertThat(result.next.next.next).isSameAs(tail); }
    @Test
    void handlesLongRange() { Solution.Node head = null; for (int i = 20; i >= 1; i--) head = new Solution.Node(i, head); assertThat(values(Solution.reverseBetween(head, 5, 16))).containsExactly(1,2,3,4,16,15,14,13,12,11,10,9,8,7,6,5,17,18,19,20); }

    private static Solution.Node list(int... values) { Solution.Node head = null; for (int i = values.length - 1; i >= 0; i--) head = new Solution.Node(values[i], head); return head; }
    private static int[] values(Solution.Node head) { int n = 0; for (Solution.Node p = head; p != null; p = p.next) n++; int[] out = new int[n]; for (int i = 0; head != null; head = head.next) out[i++] = head.value; return out; }
}
