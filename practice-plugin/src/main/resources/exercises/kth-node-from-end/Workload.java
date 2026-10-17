package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {50000, 100000, 200000, 400000, 800000};
    private Workload() {}
    static Object prepare(int size) { Solution.Node head = null; for (int i = size; i >= 1; i--) head = new Solution.Node(i, head); return new Object[]{head, size}; }
    static void run(Object state) { Object[] data = (Object[]) state; if (Solution.kthFromEnd((Solution.Node) data[0], (Integer) data[1]).value != 1) throw new IllegalStateException("Wrong kth node"); }
    static int units(int size) { return 1; }
}
