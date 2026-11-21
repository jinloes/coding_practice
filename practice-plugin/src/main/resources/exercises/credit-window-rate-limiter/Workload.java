package com.jinloes.practice;

final class Workload {
    static final String UNIT = "batch";
    static final int[] SIZES = {256, 512, 1024, 2048, 4096};

    private Workload() {
    }

    static Object prepare(int size) {
        String[] clients = new String[size];
        for (int i = 0; i < size; i++) {
            clients[i] = "client-" + i;
        }
        return clients;
    }

    static void run(Object state) {
        String[] clients = (String[]) state;
        Solution limiter = new Solution(2, 10, 4);
        int allowed = 0;
        for (int step = 0; step < 6; step++) {
            long timestamp = step * 15L;
            for (String client : clients) {
                if (limiter.allow(client, timestamp)) {
                    allowed++;
                }
            }
        }
        if (allowed <= 0) {
            throw new IllegalStateException("Expected accepted requests");
        }
    }

    static int units(int size) {
        return 1;
    }
}
