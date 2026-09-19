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

    public static Node reverse(Node head) {
        throw new UnsupportedOperationException("Implement reverse");
    }

    public static void main(String[] args) {
        assertValues(reverse(new Node(1, new Node(2, new Node(3, null)))), 3, 2, 1);
        assertValues(reverse(new Node(8, null)), 8);
        assertValues(reverse(null));
        System.out.println("Examples passed: 3");
    }

    private static void assertValues(Node head, int... expected) {
        for (int value : expected) {
            if (head == null || head.value != value) {
                throw new AssertionError("Unexpected reversed list");
            }
            head = head.next;
        }
        if (head != null) {
            throw new AssertionError("Unexpected reversed list length");
        }
    }
}
