package com.jinloes.data_structures.linked_list;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MthToLastTest {
    private ListNode<Integer> n1;
    private ListNode<Integer> n2;
    private ListNode<Integer> n3;
    private ListNode<Integer> n4;

    @BeforeEach
    public void setUp() {
        n1 = new ListNode<>(1);
        n2 = new ListNode<>(2);
        n3 = new ListNode<>(3);
        n4 = new ListNode<>(4);
        n1.next = n2;
        n2.next = n3;
        n3.next = n4;
    }

    @Test
    void mthToLast() {
        assertThat(MthToLast.mthToLast(n1, 1)).isEqualTo(n3);
        assertThat(MthToLast.mthToLast(n1, 2)).isEqualTo(n2);
        assertThat(MthToLast.mthToLast(n1, 3)).isEqualTo(n1);
        assertThat(MthToLast.mthToLast(n1, 4)).isNull();
        assertThat(MthToLast.mthToLast(n1, 100)).isNull();
    }
}