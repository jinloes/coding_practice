package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {5000, 10000, 20000, 40000, 80000};
    private Workload() {}
    static Object prepare(int size) { Solution.Node head = null; for (int i = size; i >= 1; i--) head = new Solution.Node(i, head); return new Object[]{head, size}; }
    static void run(Object state) { Object[] data = (Object[]) state; Solution.Node head = Solution.reverseBetween((Solution.Node) data[0], 2, (Integer) data[1] - 1); if (head.next.value != (Integer) data[1] - 1) throw new IllegalStateException("Wrong reversal"); }
    static int units(int size) { return 1; }
}
