package com.jinloes.practice;

import java.util.PriorityQueue;

public class Solution {
    public static class Node {
        public int value;
        public Node next;

        public Node(int value, Node next) {
            this.value = value;
            this.next = next;
        }
    }

    public static Node mergeKLists(Node[] lists) {
        PriorityQueue<Node> heap = new PriorityQueue<>(Math.max(1, lists.length), (a, b) -> Integer.compare(a.value, b.value));
        for (Node list : lists) {
            heap.add(list);
        }
        Node dummy = new Node(0, null);
        Node tail = dummy;
        while (!heap.isEmpty()) {
            Node smallest = heap.poll();
            tail.next = smallest;
            tail = smallest;
            if (smallest.next != null) {
                heap.add(smallest.next);
            }
        }
        tail.next = null;
        return dummy.next;
    }
}
