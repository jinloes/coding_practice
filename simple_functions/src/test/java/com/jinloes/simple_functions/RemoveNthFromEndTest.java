package com.jinloes.simple_functions;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RemoveNthFromEndTest {
    private ListNode head = null;

    @Before
    public void setUp() {
        ListNode n1 = new ListNode(1);
        ListNode n2 = new ListNode(2);
        ListNode n3 = new ListNode(3);
        ListNode n4 = new ListNode(4);
        ListNode n5 = new ListNode(5);

        n1.next = n2;
        n2.next = n3;
        n3.next = n4;
        n4.next = n5;

        head = n1;
    }

    @Test
    public void removeNthFromEnd() {
        ListNode result = RemoveNthFromEnd.removeNthFromEnd(head, 2);

        assertThat(result.toList()).containsExactly(1, 2, 3, 5);
    }
}