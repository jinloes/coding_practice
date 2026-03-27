package com.jinloes.linked_list;



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
            if (values.contains(current.val)) {
                previous.next = current.next;
            } else {
                values.add(current.val);
                previous = current;
            }
            current = current.next;
        }

        return head;
    }

}
