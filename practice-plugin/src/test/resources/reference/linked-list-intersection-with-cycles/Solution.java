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
    public static Node firstIntersection(Node first, Node second) { Node entryA = cycleEntry(first), entryB = cycleEntry(second); if (entryA == null && entryB == null) return acyclicIntersection(first, second, null); if (entryA == null || entryB == null) return null; if (entryA == entryB) { Node before = acyclicIntersection(first, second, entryA); return before == null ? entryA : before; } for (Node node = entryA.next; node != entryA; node = node.next) if (node == entryB) return entryA; return null; }
    private static Node cycleEntry(Node head) { Node slow = head, fast = head; while (fast != null && fast.next != null) { slow = slow.next; fast = fast.next.next; if (slow == fast) { Node entry = head; while (entry != slow) { entry = entry.next; slow = slow.next; } return entry; } } return null; }
    private static Node acyclicIntersection(Node a, Node b, Node stop) { int la = lengthUntil(a, stop), lb = lengthUntil(b, stop); while (la > lb) { a = a.next; la--; } while (lb > la) { b = b.next; lb--; } while (a != b && a != stop && b != stop) { a = a.next; b = b.next; } return a == b ? a : null; }
    private static int lengthUntil(Node node, Node stop) { int length = 0; while (node != stop) { length++; node = node.next; } return length; }
}
