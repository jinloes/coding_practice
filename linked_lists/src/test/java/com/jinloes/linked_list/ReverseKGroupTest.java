package com.jinloes.linked_list;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReverseKGroupTest {
    private ReverseKGroup reverseKGroup;

    @BeforeEach
    void setUp() {
        reverseKGroup = new ReverseKGroup();
    }

    @Test
    void reverseKGroup() {
        ListNode l1 = new ListNode(1);
        ListNode l2 = new ListNode(2);
        ListNode l3 = new ListNode(3);
        ListNode l4 = new ListNode(4);
        ListNode l5 = new ListNode(5);
        ListNode l6 = new ListNode(6);
        l1.next = l2;
        l2.next = l3;
        l3.next = l4;
        l4.next = l5;
        l5.next = l6;

        ListNode result = reverseKGroup.reverseKGroup(l1, 3);

        System.out.println(result);
    }
}