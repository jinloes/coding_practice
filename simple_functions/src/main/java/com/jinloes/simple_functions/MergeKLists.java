package com.jinloes.simple_functions;

import java.util.Comparator;
import java.util.PriorityQueue;

public class MergeKLists {
    private PriorityQueue<ListNode> priorityQueue;

    public ListNode merge(ListNode[] nodes) {
        priorityQueue = new PriorityQueue<>(nodes.length, Comparator.comparingInt(o -> o.val));

        for (ListNode node : nodes) {
            priorityQueue.offer(node);
        }

        ListNode head = null;
        ListNode current = null;

        while (!priorityQueue.isEmpty()) {
            ListNode next = priorityQueue.poll();
            if (head == null) {
                head = new ListNode(next.val);
                current = head;
            } else {
                current.next = new ListNode(next.val);
                current = current.next;
            }
            if (next.next != null) {
                priorityQueue.offer(next.next);
            }

        }
        return head;
    }
}
