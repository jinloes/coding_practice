package com.jinloes.simple_functions.linked_list;

import com.jinloes.simple_functions.ListNode;

/**
 * Find the kth last element of a singly linked list.
 */
public class KthLast {

    public ListNode kthLast(ListNode head, int k) {
        if (head == null) {
            return null;
        }
        ListNode lookAhead = head;
        ListNode current = head;
        for (int i = 1; i < k; i++) {
            lookAhead = lookAhead.getNext();
            if (lookAhead == null) {
                return null;
            }
        }

        while (lookAhead.getNext() != null) {
            lookAhead = lookAhead.getNext();
            current = current.getNext();
        }

        return current;
    }
}
