package com.jinloes.practice;

public class Solution {
    public static class Node {
        public int value;
        public Node next;
        public Node random;

        public Node(int value) {
            this.value = value;
        }
    }

    public static Node copyList(Node head) {
        Node dummy = new Node(0);
        Node tail = dummy;
        for (Node cursor = head; cursor != null; cursor = cursor.next) {
            tail.next = new Node(cursor.value);
            tail = tail.next;
            tail.random = cursor.random;
        }
        return dummy.next;
    }
}
