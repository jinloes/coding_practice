package com.jinloes.practice;

/** Scaling workload for Happy Number. n is the largest value tested; the run tests every value from 1 to n. */
final class Workload {
    static final String UNIT = "value";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        return size;
    }

    static void run(Object state) {
        int limit = (Integer) state;
        int happy = 0;
        for (int n = 1; n <= limit; n++) {
            if (Solution.isHappy(n)) {
                happy++;
            }
        }
        if (happy < 3) {
            throw new IllegalStateException("Expected happy values among 1.." + limit);
        }
    }

    static int units(int size) {
        return size;
    }
}
