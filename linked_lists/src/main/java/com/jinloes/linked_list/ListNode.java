package com.jinloes.linked_list;

import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListNode {
    int val;
    ListNode next;

    ListNode() {
    }

    ListNode(int val) {
        this.val = val;
    }

    ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ListNode)) return false;
        ListNode that = (ListNode) o;
        return val == that.val && Objects.equals(next, that.next);
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
