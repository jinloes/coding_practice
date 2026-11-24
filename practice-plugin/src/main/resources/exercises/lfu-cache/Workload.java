package com.jinloes.practice;

final class Workload {
    static final String UNIT = "run";
    static final int[] SIZES = {4_096, 8_192, 16_384, 32_768, 65_536};

    private Workload() {
    }

    static Object prepare(int size) {
        return size;
    }

    static void run(Object state) {
        int n = (Integer) state;
        int capacity = Math.max(16, n / 4);
        Solution cache = new Solution(capacity);
        long sum = 0;
        for (int i = 0; i < n; i++) {
            cache.put(i, i * 2);
            if ((i & 3) == 0) {
                sum += cache.get(i / 2);
            }
        }
        if (sum == 0) {
            throw new IllegalStateException("Unexpected zero sum");
        }
    }

    static int units(int size) {
        return 1;
    }
}
