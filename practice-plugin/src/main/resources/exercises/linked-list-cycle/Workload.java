package com.jinloes.practice;

/**
 * Scaling workload for Linked List Cycle. Input construction happens in prepare, outside the timed region,
 * so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {50000, 100000, 200000, 400000, 800000};

    private Workload() {}

    /** An acyclic list forces a solution to walk the whole chain before it can answer. */
    static Object prepare(int size) {
        Solution.Node head = null;
        for (int i = size - 1; i >= 0; i--) {
            head = new Solution.Node(i, head);
        }
        return head;
    }

    static void run(Object state) {
        if (Solution.hasCycle((Solution.Node) state)) {
            throw new IllegalStateException("Probe list is acyclic");
        }
    }

    static int units(int size) {
        return 1;
    }
}
