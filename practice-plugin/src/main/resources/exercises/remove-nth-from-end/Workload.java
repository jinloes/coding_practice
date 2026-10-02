package com.jinloes.practice;

/**
 * Scaling workload for Remove Nth From End. Input construction happens in prepare, outside the timed region,
 * so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {50000, 100000, 200000, 400000, 800000};

    private Workload() {}

    private static final class Probe {
        final Solution.Node head;
        final int k;

        Probe(Solution.Node head, int k) {
            this.head = head;
            this.k = k;
        }
    }

    /** The list is rebuilt for every repetition because removal mutates it. */
    static Object prepare(int size) {
        Solution.Node head = null;
        for (int i = size - 1; i >= 0; i--) {
            head = new Solution.Node(i, head);
        }
        return new Probe(head, size / 2);
    }

    static void run(Object state) {
        Probe probe = (Probe) state;
        Solution.Node result = Solution.removeFromEnd(probe.head, probe.k);
        if (result == null || result.value != 0 || result.next == null || result.next.value != 1) {
            throw new IllegalStateException("Probe list must keep its head");
        }
    }

    static int units(int size) {
        return 1;
    }
}
