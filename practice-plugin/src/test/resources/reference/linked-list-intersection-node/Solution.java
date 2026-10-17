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
    public static Node firstIntersection(Node first, Node second) { int a = length(first), b = length(second); while (a > b) { first = first.next; a--; } while (b > a) { second = second.next; b--; } while (first != second) { first = first == null ? null : first.next; second = second == null ? null : second.next; } return first; }
    private static int length(Node node) { int n = 0; while (node != null) { n++; node = node.next; } return n; }
}
