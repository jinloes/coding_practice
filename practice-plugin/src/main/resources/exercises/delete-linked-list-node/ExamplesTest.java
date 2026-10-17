package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExamplesTest {
    @Test
    void deletesMiddleNodeByCopyingSuccessor() { Solution.Node head = list(1,2,3,4); Solution.deleteNode(head.next); assertThat(values(head)).containsExactly(1,3,4); }
    @Test
    void deletesFirstNodeWhenItHasSuccessor() { Solution.Node head = list(7,8); Solution.deleteNode(head); assertThat(values(head)).containsExactly(8); }
    @Test
    void rejectsTailNode() { Solution.Node head = list(1,2); assertThatThrownBy(() -> Solution.deleteNode(head.next)).isInstanceOf(IllegalArgumentException.class); }
    private static Solution.Node list(int... values) { Solution.Node head = null; for (int i = values.length - 1; i >= 0; i--) head = new Solution.Node(values[i], head); return head; }
    private static int[] values(Solution.Node head) { int n = 0; for (Solution.Node p = head; p != null; p = p.next) n++; int[] out = new int[n]; for (int i = 0; head != null; head = head.next) out[i++] = head.value; return out; }
}
