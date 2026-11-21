package com.jinloes.practice;

final class Workload {
    static final String UNIT = "batch";
    static final int[] SIZES = {256, 512, 1024, 2048, 4096};

    private Workload() {
    }

    static Object prepare(int size) {
        String[] clients = new String[size];
        for (int i = 0; i < size; i++) {
            clients[i] = "c" + i;
        }
        return clients;
    }

    static void run(Object state) {
        String[] clients = (String[]) state;
        Solution limiter = new Solution(3, 10);
        int accepted = 0;
        for (int round = 0; round < 5; round++) {
            long timestamp = round * 4L;
            for (String client : clients) {
                if (limiter.allow(client, timestamp)) {
                    accepted++;
                }
            }
        }
        if (accepted <= 0) {
            throw new IllegalStateException("Expected accepted requests");
        }
    }

    static int units(int size) {
        return 1;
    }
}
