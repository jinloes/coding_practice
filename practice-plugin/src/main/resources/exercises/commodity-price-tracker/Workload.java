package com.jinloes.practice;

final class Workload {
    static final String UNIT = "run";
    static final int[] SIZES = {4_096, 8_192, 16_384, 32_768, 65_536};

    private Workload() {
    }

    static Object prepare(int size) {
        int[][] updates = new int[size][2];
        int half = Math.max(1, size / 2);
        for (int i = 0; i < size; i++) {
            int ts = (i % half) + 1;
            int price = 1 + ((i * 73) % 10_000);
            updates[i] = new int[]{ts, price};
        }
        return updates;
    }

    static void run(Object state) {
        int[][] updates = (int[][]) state;
        Solution tracker = new Solution();
        long sum = 0;
        for (int[] u : updates) {
            tracker.update(u[0], u[1]);
            sum += tracker.maxPrice() + tracker.minPrice() + tracker.latest();
        }
        if (sum == 0) {
            throw new IllegalStateException("Unreachable");
        }
    }

    static int units(int size) {
        return 1;
    }
}
