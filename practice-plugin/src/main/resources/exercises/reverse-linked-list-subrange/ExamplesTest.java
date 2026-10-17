package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void reversesMiddleRange() { assertThat(values(Solution.reverseBetween(list(1,2,3,4,5), 2, 4))).containsExactly(1,4,3,2,5); }
    @Test
    void reversesWholeList() { assertThat(values(Solution.reverseBetween(list(1,2,3), 1, 3))).containsExactly(3,2,1); }
    @Test
    void leavesSinglePositionRangeAlone() { assertThat(values(Solution.reverseBetween(list(6,7,8), 2, 2))).containsExactly(6,7,8); }

    private static Solution.Node list(int... values) { Solution.Node head = null; for (int i = values.length - 1; i >= 0; i--) head = new Solution.Node(values[i], head); return head; }
    private static int[] values(Solution.Node head) { int n = 0; for (Solution.Node p = head; p != null; p = p.next) n++; int[] out = new int[n]; for (int i = 0; head != null; head = head.next) out[i++] = head.value; return out; }
}
