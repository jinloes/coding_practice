package com.jinloes.practice;

/**
 * Scaling workload for Reorder List. Input construction happens in prepare, outside the timed region,
 * so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {50000, 100000, 200000, 400000, 800000};

    private Workload() {}

    /** The list is rebuilt for every repetition because reordering mutates it in place. */
    static Object prepare(int size) {
        Solution.Node head = null;
        for (int i = size - 1; i >= 0; i--) {
            head = new Solution.Node(i, head);
        }
        return head;
    }

    static void run(Object state) {
        Solution.Node head = (Solution.Node) state;
        Solution.reorder(head);
        if (head.next == null || head.next.value == 1 || head.next.next == null || head.next.next.value != 1) {
            throw new IllegalStateException("Probe list was not reordered");
        }
    }

    static int units(int size) {
        return 1;
    }
}
