package com.jinloes.practice;

public class Solution {
    public static class Node {
        public int value;
        public Node next;

        public Node(int value, Node next) { this.value = value; this.next = next; }
    }
    public static Node reverseBetween(Node head, int start, int end) { Node prev = null, cur = head; while (cur != null) { Node next = cur.next; cur.next = prev; prev = cur; cur = next; } return prev; }
}
