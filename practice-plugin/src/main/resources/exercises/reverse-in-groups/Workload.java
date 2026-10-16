package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private static final int GROUP = 3;

    private Workload() {}

    static Object prepare(int size) {
        Solution.ListNode dummy = new Solution.ListNode(0);
        Solution.ListNode tail = dummy;
        for (int i = 1; i <= size; i++) {
            tail.next = new Solution.ListNode(i);
            tail = tail.next;
        }
        return dummy.next;
    }

    static void run(Object state) {
        Solution.ListNode result = Solution.reverseInGroups((Solution.ListNode) state, GROUP);
        if (result == null || result.val != GROUP || result.next == null || result.next.val != GROUP - 1) {
            throw new IllegalStateException("The first group was not reversed");
        }
    }

    static int units(int size) { return 1; }
}
