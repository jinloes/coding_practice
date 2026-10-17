package com.jinloes.practice;

public class Solution {
    public static class Node {
        public int value;
        public Node next;

        public Node(int value, Node next) { this.value = value; this.next = next; }
    }
    public static Node kthFromEnd(Node head, int k) { if (k < 1) throw new IllegalArgumentException("k must be positive"); for (int i = 1; head != null && i < k; i++) head = head.next; return head; }
}
