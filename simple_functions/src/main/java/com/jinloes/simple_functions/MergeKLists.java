package com.jinloes.simple_functions;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * You are given an array of k linked-lists lists, each linked-list is sorted in ascending order.
 * <p>
 * Merge all the linked-lists into one sorted linked-list and return it.
 */
public class MergeKLists {
    private PriorityQueue<ListNode> priorityQueue;

    public ListNode merge(ListNode[] nodes) {
        priorityQueue = new PriorityQueue<>(nodes.length, Comparator.comparingInt(o -> o.val));

        for (ListNode node : nodes) {
            if (node != null) {
                priorityQueue.offer(node);
            }
        }

        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        while (!priorityQueue.isEmpty()) {
            ListNode next = priorityQueue.poll();
            current.next = new ListNode(next.val);
            current = current.next;
            if (next.next != null) {
                priorityQueue.offer(next.next);
            }

        }
        return dummy.next;
    }
}
