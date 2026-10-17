package com.jinloes.practice;

public class Solution {
    public static class Node {
        public int value;
        public Node next;

        public Node(int value, Node next) { this.value = value; this.next = next; }
    }
    public static Node kthFromEnd(Node head, int k) { if (k < 1) throw new IllegalArgumentException("k must be positive"); Node lead = head; for (int i = 0; i < k; i++) { if (lead == null) return null; lead = lead.next; } Node answer = head; while (lead != null) { lead = lead.next; answer = answer.next; } return answer; }
}
