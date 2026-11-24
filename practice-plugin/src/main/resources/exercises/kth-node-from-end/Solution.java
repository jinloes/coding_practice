package com.jinloes.practice;

public class Solution {
    public static class Node {
        public int value;
        public Node next;

        public Node(int value, Node next) { this.value = value; this.next = next; }

        @Override
        public String toString() {
            return "Node(value=" + value + ")";
        }
    }
    public static Node kthFromEnd(Node head, int k) { throw new UnsupportedOperationException("Implement kthFromEnd"); }
}
