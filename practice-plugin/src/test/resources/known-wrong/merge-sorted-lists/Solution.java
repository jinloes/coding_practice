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
    public static Node merge(Node first, Node second) {
        return first != null ? first : second;
    }
}
