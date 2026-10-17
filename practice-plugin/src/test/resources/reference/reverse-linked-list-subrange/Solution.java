package com.jinloes.practice;

public class Solution {
    public static class Node {
        public int value;
        public Node next;

        public Node(int value, Node next) { this.value = value; this.next = next; }
    }
    public static Node reverseBetween(Node head, int start, int end) { Node dummy = new Node(0, head); Node before = dummy; for (int i = 1; i < start; i++) before = before.next; Node current = before.next; for (int i = 0; i < end - start; i++) { Node moved = current.next; current.next = moved.next; moved.next = before.next; before.next = moved; } return dummy.next; }
}
