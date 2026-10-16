package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        Solution.ListNode[] heads = new Solution.ListNode[2];
        for (int list = 0; list < 2; list++) {
            Solution.ListNode dummy = new Solution.ListNode(0);
            Solution.ListNode tail = dummy;
            for (int i = 0; i < size; i++) {
                tail.next = new Solution.ListNode((i * (list == 0 ? 7 : 3) + 5 + list) % 10);
                tail = tail.next;
            }
            heads[list] = dummy.next;
        }
        return heads;
    }

    static void run(Object state) {
        Solution.ListNode[] heads = (Solution.ListNode[]) state;
        Solution.ListNode result = null;
        for (int repeat = 0; repeat < 4; repeat++) {
            result = Solution.addDigitLists(heads[0], heads[1]);
        }
        int expectedHead = (heads[0].val + heads[1].val) % 10;
        if (result == null || result.val != expectedHead) {
            throw new IllegalStateException("Unexpected least significant digit");
        }
    }

    static int units(int size) { return 1; }
}
