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
    public static Node firstIntersection(Node first, Node second) { while (first != null && second != null) { if (first.value == second.value) return first; first = first.next; second = second.next; } return null; }
}
