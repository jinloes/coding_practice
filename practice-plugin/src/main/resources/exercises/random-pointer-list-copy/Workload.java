package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        Solution.Node[] nodes = new Solution.Node[size];
        for (int i = 0; i < size; i++) {
            nodes[i] = new Solution.Node(i);
            if (i > 0) {
                nodes[i - 1].next = nodes[i];
            }
        }
        for (int i = 0; i < size; i += 2) {
            nodes[i].random = nodes[(int) ((i * 31L + 7) % size)];
        }
        return nodes[0];
    }

    static void run(Object state) {
        Solution.Node head = (Solution.Node) state;
        Solution.Node copy = Solution.copyList(head);
        if (copy == null || copy == head || copy.value != head.value || copy.random == head.random
                || copy.next == head.next) {
            throw new IllegalStateException("Result is not a separate copy of the list");
        }
    }

    static int units(int size) { return 1; }
}
