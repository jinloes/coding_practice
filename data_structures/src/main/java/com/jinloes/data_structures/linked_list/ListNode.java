package com.jinloes.data_structures.linked_list;

import com.google.common.base.MoreObjects;

/**
 * Singly linked list node.
 *
 * Getters/setters omitted for simplicity.
 *
 * @param <T> data type
 */
public class ListNode<T> {
    ListNode<T> next;
    T data;

    public ListNode() {
    }

    public ListNode(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("data", data)
                .toString();
    }
}