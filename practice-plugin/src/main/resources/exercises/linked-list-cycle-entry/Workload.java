package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {50000, 100000, 200000, 400000, 800000};
    private Workload() {}
    static Object prepare(int size) { Solution.Node[] nodes = new Solution.Node[size]; for (int i = 0; i < size; i++) nodes[i] = new Solution.Node(i, null); for (int i = 0; i + 1 < size; i++) nodes[i].next = nodes[i + 1]; nodes[size - 1].next = nodes[size / 2]; return nodes; }
    static void run(Object state) { Solution.Node[] nodes = (Solution.Node[]) state; if (Solution.cycleEntry(nodes[0]) != nodes[nodes.length / 2]) throw new IllegalStateException("Wrong entry"); }
    static int units(int size) { return 1; }
}
