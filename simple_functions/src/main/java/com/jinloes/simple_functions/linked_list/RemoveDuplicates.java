package com.jinloes.simple_functions.linked_list;

import com.jinloes.simple_functions.ListNode;

import java.util.HashSet;
import java.util.Set;

/**
 * Remove duplicates from a linked list.
 */
public class RemoveDuplicates {

    public ListNode removeDuplicates(ListNode head) {
        Set<Integer> values = new HashSet<>();

        ListNode current = head;
        ListNode previous = null;
        while (current != null) {
            if (values.contains(current.getVal())) {
                previous.setNext(current.getNext());
            } else {
                values.add(current.getVal());
                previous = current;
            }
            current = current.getNext();
        }

        return head;
    }

}
