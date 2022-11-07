package com.jinloes.data_structures.linked_list;

/**
 * Design a stack using a linked list.
 */
public class Stack<T> {
    private ListNode<T> head;
    private final ListNode<T> end;

    public Stack() {
        this.head = new ListNode<>();
        this.end = head;
    }

    void push(T val) {
        ListNode<T> newNode = new ListNode<>(val);
        newNode.next = head;
        head = newNode;
    }

    public T peek() {
        return head.data;
    }

    public T pop() {
        if (head == end) {
            return null;
        }
        ListNode<T> toRemove = head;
        head = head.next;
        return toRemove.data;
    }
}
