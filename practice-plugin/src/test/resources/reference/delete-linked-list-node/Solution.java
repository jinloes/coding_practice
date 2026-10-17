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
    public static void deleteNode(Node node) { if (node == null || node.next == null) throw new IllegalArgumentException("node must be non-tail"); node.value = node.next.value; node.next = node.next.next; }
}
