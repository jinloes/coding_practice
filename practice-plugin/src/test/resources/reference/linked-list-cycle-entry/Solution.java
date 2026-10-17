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
    public static Node cycleEntry(Node head) { Node slow = head, fast = head; while (fast != null && fast.next != null) { slow = slow.next; fast = fast.next.next; if (slow == fast) { Node entry = head; while (entry != slow) { entry = entry.next; slow = slow.next; } return entry; } } return null; }
}
