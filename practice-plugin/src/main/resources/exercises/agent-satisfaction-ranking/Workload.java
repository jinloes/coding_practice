package com.jinloes.practice;

import java.util.List;

final class Workload {
    static final String UNIT = "ranking";
    static final int[] SIZES = {1024, 2048, 4096, 8192, 16384};

    private Workload() {
    }

    static Object prepare(int size) {
        Solution tracker = new Solution();
        for (int i = 0; i < size; i++) {
            String agent = "agent-" + ((i * 7919) % size);
            tracker.recordScore(agent, (i % 5) + 1);
            tracker.recordScore(agent, ((i * 3 + 1) % 5) + 1);
            if ((i & 1) == 0) {
                tracker.recordScore(agent, 5);
            }
        }
        return new Object[]{tracker, size};
    }

    static void run(Object state) {
        Object[] data = (Object[]) state;
        Solution tracker = (Solution) data[0];
        int size = (Integer) data[1];
        List<String> ranked = tracker.qualifiedAgents(2);
        if (ranked.size() != size) {
            throw new IllegalStateException("Unexpected ranked count: " + ranked.size());
        }
    }

    static int units(int size) {
        return 1;
    }
}
