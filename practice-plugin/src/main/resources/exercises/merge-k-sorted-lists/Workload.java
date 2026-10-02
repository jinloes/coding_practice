package com.jinloes.practice;

/**
 * Scaling workload for Merge K Sorted Lists. Input construction happens in prepare, outside the timed region,
 * so only the learner's own work is measured. The list count is fixed and each list grows with the node count.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {8192, 16384, 32768, 65536, 131072};
    private static final int LIST_COUNT = 64;

    private Workload() {}

    /** List j holds j, j + k, j + 2k, ... so every merge step has to pick between many different lists. */
    static Object prepare(int size) {
        int k = LIST_COUNT;
        int perList = size / k;
        Solution.Node[] lists = new Solution.Node[k];
        for (int j = 0; j < k; j++) {
            Solution.Node head = null;
            for (int t = perList - 1; t >= 0; t--) {
                head = new Solution.Node(j + t * k, head);
            }
            lists[j] = head;
        }
        return lists;
    }

    static void run(Object state) {
        Solution.Node[] lists = (Solution.Node[]) state;
        Solution.Node result = Solution.mergeKLists(lists);
        if (result == null || result.value != 0) {
            throw new IllegalStateException("Probe merge must start with its smallest value");
        }
        if (result.next == null || result.next.value != 1) {
            throw new IllegalStateException("Probe merge must continue with the next smallest value");
        }
    }

    static int units(int size) {
        return 1;
    }
}
