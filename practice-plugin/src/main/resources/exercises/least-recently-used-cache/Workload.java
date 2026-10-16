package com.jinloes.practice;

final class Workload {
    static final String UNIT = "operation";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        return new int[]{size};
    }

    /** Fills a cache to capacity, reads every key back, then forces evictions with fresh keys. */
    static void run(Object state) {
        int size = ((int[]) state)[0];
        Solution cache = new Solution(size);
        for (int i = 0; i < size; i++) {
            cache.put(i, i);
        }
        long total = 0;
        for (int i = 0; i < size; i++) {
            total += cache.get(i);
        }
        for (int i = 0; i < size; i++) {
            cache.put(size + i, i);
        }
        if (total != (long) size * (size - 1) / 2 || cache.get(size) != 0 || cache.get(0) != -1) {
            throw new IllegalStateException("Cache returned unexpected values");
        }
    }

    static int units(int size) {
        return 1;
    }
}
