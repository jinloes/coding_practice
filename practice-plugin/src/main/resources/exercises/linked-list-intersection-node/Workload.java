package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {50000, 100000, 200000, 400000, 800000};
    private Workload() {}
    static Object prepare(int size) { Solution.Node shared = chain(size / 2, 10_000_000); return new Solution.Node[]{append(chain(size, 0), shared), append(chain(size / 3, 1_000_000), shared), shared}; }
    static void run(Object state) { Solution.Node[] nodes = (Solution.Node[]) state; if (Solution.firstIntersection(nodes[0], nodes[1]) != nodes[2]) throw new IllegalStateException("Wrong intersection"); }
    static int units(int size) { return 1; }
    private static Solution.Node chain(int count, int start) { Solution.Node head = null; for (int i = count - 1; i >= 0; i--) head = new Solution.Node(start + i, head); return head; }
    private static Solution.Node append(Solution.Node prefix, Solution.Node tail) { if (prefix == null) return tail; Solution.Node p = prefix; while (p.next != null) p = p.next; p.next = tail; return prefix; }
}
