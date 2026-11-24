package com.jinloes.practice;

import java.util.List;

final class Workload {
    static final String UNIT = "run";
    static final int[] SIZES = {4_096, 8_192, 16_384, 32_768, 65_536};

    private Workload() {
    }

    static Object prepare(int size) {
        int[][] orders = new int[size][4];
        for (int i = 0; i < size; i++) {
            boolean isBuy = (i & 1) == 1;
            int price = 1000 + ((i * 37) % 200);
            int qty = 1 + (i % 10);
            orders[i] = new int[]{i + 1, isBuy ? 1 : 0, price, qty};
        }
        return orders;
    }

    static void run(Object state) {
        int[][] orders = (int[][]) state;
        Solution book = new Solution();
        long trades = 0;
        for (int[] order : orders) {
            List<Solution.Trade> filled = book.submitOrder(order[0], order[1] == 1, order[2], order[3]);
            trades += filled.size();
        }
        if (trades == -1) {
            throw new IllegalStateException("Unreachable");
        }
    }

    static int units(int size) {
        return 1;
    }
}
