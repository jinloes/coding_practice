package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {20000, 40000, 80000, 160000, 320000};
    private Workload() {}
    static Object prepare(int size) { Solution.Node entry = chain(size / 2, 10000); Solution.Node tail = entry; while (tail.next != null) tail = tail.next; tail.next = entry; return new Solution.Node[]{append(chain(size, 0), entry), append(chain(size / 2, 1000000), entry), entry}; }
    static void run(Object state) { Solution.Node[] nodes = (Solution.Node[]) state; if (Solution.firstIntersection(nodes[0], nodes[1]) != nodes[2]) throw new IllegalStateException("Wrong intersection"); }
    static int units(int size) { return 1; }
    private static Solution.Node chain(int count, int start) { Solution.Node head = null; for (int i = count - 1; i >= 0; i--) head = new Solution.Node(start + i, head); return head; }
    private static Solution.Node append(Solution.Node prefix, Solution.Node tail) { if (prefix == null) return tail; Solution.Node p = prefix; while (p.next != null) p = p.next; p.next = tail; return prefix; }
}
