package com.jinloes.practice;

final class Workload {
    static final String UNIT = "deletion";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private Workload() {}
    static Object prepare(int size) { Solution.Node[] nodes = new Solution.Node[size]; for (int i = 0; i < size; i++) nodes[i] = new Solution.Node(i, new Solution.Node(i + 1, null)); return nodes; }
    static void run(Object state) { for (Solution.Node node : (Solution.Node[]) state) Solution.deleteNode(node); }
    static int units(int size) { return 1; }
}
