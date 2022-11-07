package com.jinloes.data_structures.linked_list;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class DeleteNodeTest {
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
    void deleteNode() {
        assertThat(DeleteNode.deleteNode(n1, 2)).isEqualTo(n1);

        ListNode<Integer> current = n1;
        assertThat(current.data).isEqualTo(1);

        current = current.next;
        assertThat(current.data).isEqualTo(3);
    }

    @Test
    void deleteHeadNode() {
        ListNode<Integer> n1 = new ListNode<>(1);
        ListNode<Integer> n2 = new ListNode<>(2);
        ListNode<Integer> n3 = new ListNode<>(3);
        n1.next = n2;
        n2.next = n3;

        assertThat(DeleteNode.deleteNode(n1, 1)).isEqualTo(n2);

        assertThat(n1.next).isNull();

        ListNode<Integer> current = n2;
        assertThat(current.data).isEqualTo(2);

        current = current.next;
        assertThat(current.data).isEqualTo(3);
    }

    @Test
    void deleteTailNode() {
        ListNode<Integer> n1 = new ListNode<>(1);
        ListNode<Integer> n2 = new ListNode<>(2);
        ListNode<Integer> n3 = new ListNode<>(3);
        n1.next = n2;
        n2.next = n3;

        assertThat(DeleteNode.deleteNode(n1, 3)).isEqualTo(n1);

        ListNode<Integer> current = n1;
        assertThat(current.data).isEqualTo(1);

        current = current.next;
        assertThat(current.data).isEqualTo(2);

        assertThat(current.next).isNull();
    }
}