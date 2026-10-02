package com.jinloes.practice;

public class Solution {
    public static class Node {
        public int value;
        public Node next;

        public Node(int value, Node next) {
            this.value = value;
            this.next = next;
        }
    }

    public static void reorder(Node head) {
        if (head == null || head.next == null) {
            return;
        }
        Node slow = head;
        Node fast = head;
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        Node second = slow.next;
        slow.next = null;
        Node previous = null;
        while (second != null) {
            Node next = second.next;
            second.next = previous;
            previous = second;
            second = next;
        }
        Node first = head;
        second = previous;
        while (second != null) {
            Node firstNext = first.next;
            Node secondNext = second.next;
            first.next = second;
            second.next = firstNext;
            first = firstNext;
            second = secondNext;
        }
    }
}
