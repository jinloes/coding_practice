package com.jinloes.practice;

public class Solution {
    public static class Node {
        public int value;
        public Node next;
        public Node random;

        public Node(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Node(value=" + value + ")";
        }
    }

    public static Node copyList(Node head) {
        throw new UnsupportedOperationException("Implement copyList");
    }
}
