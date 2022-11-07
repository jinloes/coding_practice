package com.jinloes.data_structures.linked_list;

import java.util.Objects;

/**
 * Insert a value into a single listed list after a given value.
 */
public class InsertAfter {
    public static <T> boolean insertAfter(ListNode<T> head, T insertionPoint, T valToInsert) {
        if (head == null) {
            return false;
        }

        ListNode<T> newNode = new ListNode<>(valToInsert);

        if (Objects.equals(head.data, insertionPoint)) {
            newNode.next = head.next;
            head.next = newNode;
            return true;
        }
        ListNode<T> current = head;
        while (current != null) {
            if (current.next != null && Objects.equals(current.next.data, insertionPoint)) {
                newNode.next = current.next.next;
                current.next.next = newNode;
                return true;
            }
            current = current.next;
        }

        return false;
    }
}
