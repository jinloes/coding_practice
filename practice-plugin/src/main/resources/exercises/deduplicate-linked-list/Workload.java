package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {1000, 2000, 4000, 8000, 16000};
    private Workload() {}
    static Object prepare(int size) { return new int[]{size}; }
    static void run(Object state) {
        int size = ((int[]) state)[0];
        Solution.Node head = null;
        for (int i = size - 1; i >= 0; i--) {
            head = new Solution.Node((i * 7919) % (size / 2 + 1), head);
        }
        if (Solution.removeDuplicates(head) == null) throw new IllegalStateException("Unexpected empty list");
    }
    static int units(int size) { return 1; }
}