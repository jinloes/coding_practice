package com.jinloes.practice;

public class Solution {
    public static class Node {
        public int value;
        public Node left;
        public Node right;
        public Node next;

        public Node(int value) { this.value = value; }
        public Node(int value, Node left, Node right) { this.value = value; this.left = left; this.right = right; }
    }

    public static Node connect(Node root) {
        throw new UnsupportedOperationException("Implement connect");
    }
}
