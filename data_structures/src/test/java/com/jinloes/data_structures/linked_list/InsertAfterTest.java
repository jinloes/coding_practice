package com.jinloes.data_structures.linked_list;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class InsertAfterTest {
    private ListNode<Integer> n1;
    private ListNode<Integer> n2;
    private ListNode<Integer> n3;

    @BeforeEach
    public void setUp() {
        n1 = new ListNode<>(1);
        n2 = new ListNode<>(2);
        n3 = new ListNode<>(3);
        n1.next = n2;
        n2.next = n3;
    }

    @Test
    void insertAfter() {
        InsertAfter.insertAfter(n1, 2, 4);

        ListNode<Integer> current = n1;
        assertThat(current.data).isEqualTo(1);

        current = current.next;
        assertThat(current.data).isEqualTo(2);

        current = current.next;
        assertThat(current.data).isEqualTo(4);

        current = current.next;
        assertThat(current.data).isEqualTo(3);

        current = current.next;
        assertThat(current).isNull();

        InsertAfter.insertAfter(n1, 3, 5);
        current = n1;
        assertThat(current.data).isEqualTo(1);

        current = current.next;
        assertThat(current.data).isEqualTo(2);

        current = current.next;
        assertThat(current.data).isEqualTo(4);

        current = current.next;
        assertThat(current.data).isEqualTo(3);

        current = current.next;
        assertThat(current.data).isEqualTo(5);

        current = current.next;
        assertThat(current).isNull();
    }

    @Test
    void insertAfterHead() {
        InsertAfter.insertAfter(n1, 1, 4);

        ListNode<Integer> current = n1;
        assertThat(current.data).isEqualTo(1);

        current = current.next;
        assertThat(current.data).isEqualTo(4);

        current = current.next;
        assertThat(current.data).isEqualTo(2);

        current = current.next;
        assertThat(current.data).isEqualTo(3);

        current = current.next;
        assertThat(current).isNull();
    }
}