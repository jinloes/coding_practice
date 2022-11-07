package com.jinloes.data_structures.linked_list;

import java.util.Objects;

/**
 * Delete a node from a singly linked list
 */
public class DeleteNode {
    public static <T> ListNode<T> deleteNode(ListNode<T> head, T valToDelete) {
        if (head == null) {
            return head;
        }

        if (Objects.equals(head.data, valToDelete)) {
            ListNode<T> newHead = head.next;
            head.next = null;
            return newHead;
        }

        ListNode<T> current = head;
        while (current != null) {
            if (current.next != null && Objects.equals(current.next.data, valToDelete)) {
                current.next = current.next.next;
                return head;
            }
            current = current.next;
        }

        return head;
    }
}
