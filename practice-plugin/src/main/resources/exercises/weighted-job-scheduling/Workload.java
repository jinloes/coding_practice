package com.jinloes.practice;

final class Workload {
    static final String UNIT = "run";
    static final int[] SIZES = {4_096, 8_192, 16_384, 32_768, 65_536};

    private Workload() {
    }

    static Object prepare(int size) {
        int[] starts = new int[size];
        int[] ends = new int[size];
        int[] profits = new int[size];
        for (int i = 0; i < size; i++) {
            int s = (i * 37) % (size * 2);
            starts[i] = s;
            ends[i] = s + 1 + (i % 15);
            profits[i] = 1 + ((i * 53) % 100);
        }
        return new int[][]{starts, ends, profits};
    }

    static void run(Object state) {
        int[][] data = (int[][]) state;
        int result = new Solution().maxProfit(data[0].clone(), data[1].clone(), data[2].clone());
        if (result <= 0) {
            throw new IllegalStateException("Expected positive profit");
        }
    }

    static int units(int size) {
        return 1;
    }
}
