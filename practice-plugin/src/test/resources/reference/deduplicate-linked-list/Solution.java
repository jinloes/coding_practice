package com.jinloes.practice;

import java.util.HashSet;
import java.util.Set;

public class Solution {
    public static class Node {
        public int value;
        public Node next;

        public Node(int value, Node next) { this.value = value; this.next = next; }
    }
    public static Node removeDuplicates(Node head) { Set<Integer> seen = new HashSet<>(); Node current = head, previous = null; while (current != null) { if (!seen.add(current.value)) previous.next = current.next; else previous = current; current = current.next; } return head; }
}
