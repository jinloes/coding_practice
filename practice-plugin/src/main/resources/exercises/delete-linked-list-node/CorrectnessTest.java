package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorrectnessTest {
    @Test
    void deletesNodeBeforeTail() { Solution.Node head = list(1,2,3); Solution.deleteNode(head.next); assertThat(values(head)).containsExactly(1,3); }
    @Test
    void keepsFollowingSuffix() { Solution.Node head = list(4,5,6,7,8); Solution.deleteNode(head.next.next); assertThat(values(head)).containsExactly(4,5,7,8); }
    @Test
    void supportsRepeatedValues() { Solution.Node head = list(1,9,9,2); Solution.deleteNode(head.next); assertThat(values(head)).containsExactly(1,9,2); }
    @Test
    void rejectsNull() { assertThatThrownBy(() -> Solution.deleteNode(null)).isInstanceOf(IllegalArgumentException.class); }
    @Test
    void canApplyManyIndependentDeletions() { for (int i = 0; i < 100; i++) { Solution.Node head = list(i, i + 1, i + 2); Solution.deleteNode(head); assertThat(values(head)).containsExactly(i + 1, i + 2); } }
    private static Solution.Node list(int... values) { Solution.Node head = null; for (int i = values.length - 1; i >= 0; i--) head = new Solution.Node(values[i], head); return head; }
    private static int[] values(Solution.Node head) { int n = 0; for (Solution.Node p = head; p != null; p = p.next) n++; int[] out = new int[n]; for (int i = 0; head != null; head = head.next) out[i++] = head.value; return out; }
}
