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
        Solution rm = new Solution();
        for (int i = 0; i < n; i++) {
            int base = i * 10;
            rm.addRange(base, base + 6);
        }
        for (int i = 0; i < n; i += 2) {
            int base = i * 10;
            rm.removeRange(base + 2, base + 4);
        }
        int hits = 0;
        for (int i = 0; i < n; i++) {
            int base = i * 10;
            if (rm.queryRange(base, base + 2)) {
                hits++;
            }
        }
        if (hits != n) {
            throw new IllegalStateException("Expected " + n + " hits, got " + hits);
        }
    }

    static int units(int size) {
        return 1;
    }
}
