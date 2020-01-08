package com.jinloes.simple_functions;

import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListNode {
    int val;
    ListNode next;

    ListNode(int x) {
        val = x;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListNode listNode = (ListNode) o;
        return val == listNode.val &&
                Objects.equals(next, listNode.next);
    }

    public List<Integer> toList() {
        List<Integer> list = new ArrayList<>();
        ListNode current = this;
        while (current != null) {
            list.add(current.val);
            current = current.next;
        }

        return list;
    }

    @Override
    public int hashCode() {
        return Objects.hash(val, next);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("val", val)
                .add("next", next)
                .toString();
    }
}