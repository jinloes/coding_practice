package com.jinloes.practice;

public class Solution {
    public static class Node {
        public int value;
        public Node next;

        public Node(int value, Node next) { this.value = value; this.next = next; }
    }
    public static Node removeDuplicates(Node head) { Node node = head; while (node != null && node.next != null) { if (node.value == node.next.value) node.next = node.next.next; else node = node.next; } return head; }
}
