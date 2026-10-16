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
        if (head == null) {
            return null;
        }
        for (Node cursor = head; cursor != null; cursor = cursor.next.next) {
            Node twin = new Node(cursor.value);
            twin.next = cursor.next;
            cursor.next = twin;
        }
        for (Node cursor = head; cursor != null; cursor = cursor.next.next) {
            if (cursor.random != null) {
                cursor.next.random = cursor.random.next;
            }
        }
        Node copyHead = head.next;
        for (Node cursor = head; cursor != null; ) {
            Node twin = cursor.next;
            cursor.next = twin.next;
            twin.next = cursor.next == null ? null : cursor.next.next;
            cursor = cursor.next;
        }
        return copyHead;
    }
}
