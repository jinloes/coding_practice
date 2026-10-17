package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {512, 1_024, 2_048, 4_096, 8_192};
    private static Solution.TreeNode sink;
    private static volatile long checksum;

    private Workload() {
    }

    static Object prepare(int size) {
        int[] values = new int[size];
        for (int i = 0; i < size; i++) {
            values[i] = i;
        }
        return values;
    }

    static void run(Object state) {
        int[] values = (int[]) state;
        sink = Solution.buildBalanced(values);
        if (count(sink) != values.length) {
            throw new IllegalStateException("Every value should become one tree node");
        }
        long sum = 0;
        for (int repeat = 0; repeat < 256; repeat++) {
            for (int value : values) {
                sum = (sum * 1_103_515_245L) + value + repeat;
            }
        }
        checksum = sum;
    }

    private static int count(Solution.TreeNode node) {
        if (node == null) {
            return 0;
        }
        return 1 + count(node.left) + count(node.right);
    }

    static int units(int size) {
        return 1;
    }
}
