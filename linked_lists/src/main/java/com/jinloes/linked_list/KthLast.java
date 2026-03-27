package com.jinloes.linked_list;



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
            lookAhead = lookAhead.next;
            if (lookAhead == null) {
                return null;
            }
        }

        while (lookAhead.next != null) {
            lookAhead = lookAhead.next;
            current = current.next;
        }

        return current;
    }
}
