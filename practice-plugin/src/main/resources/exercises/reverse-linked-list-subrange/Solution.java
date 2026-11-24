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
    public static Node reverseBetween(Node head, int start, int end) { throw new UnsupportedOperationException("Implement reverseBetween"); }
}
