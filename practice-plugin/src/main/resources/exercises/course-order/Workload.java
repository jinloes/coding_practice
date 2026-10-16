package com.jinloes.practice;

/** Scaling workload for Course Order: a single chain of "size" courses. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        int[][] chain = new int[size - 1][];
        for (int i = 0; i < size - 1; i++) {
            chain[i] = new int[]{i + 1, i};
        }
        return new Object[]{size, chain};
    }

    static void run(Object state) {
        Object[] input = (Object[]) state;
        int courses = (Integer) input[0];
        int[] order = Solution.courseOrder(courses, (int[][]) input[1]);
        if (order.length != courses || order[0] != 0 || order[courses - 1] != courses - 1) {
            throw new IllegalStateException("Unexpected course order");
        }
    }

    static int units(int size) {
        return 1;
    }
}
