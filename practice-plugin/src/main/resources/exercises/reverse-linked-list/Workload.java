package com.jinloes.practice;

/**
 * Scaling workload for Reverse Linked List. Input construction happens in prepare, outside the timed region,
 * so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {2000, 4000, 8000, 16000, 32000};

    private Workload() {}

    static Object prepare(int size) {
        Solution.Node head = null;
        for (int i = size - 1; i >= 0; i--) {
            head = new Solution.Node(i, head);
        }
        return head;
    }

    /** The list is rebuilt for every repetition because reversing is allowed to mutate it. */
    static void run(Object state) {
        if (Solution.reverse((Solution.Node) state) == null) {
            throw new IllegalStateException("Probe input is not empty");
        }
    }

    static int units(int size) {
        return 1;
    }
}
