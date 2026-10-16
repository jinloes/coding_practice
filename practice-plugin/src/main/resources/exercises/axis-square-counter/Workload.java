package com.jinloes.practice;

/**
 * Scaling workload for Axis Square Counter. n is the number of distinct points, laid out on a grid 256 wide.
 * One run adds all of them and then asks four counts from the origin.
 */
final class Workload {
    static final String UNIT = "run";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        return size;
    }

    static void run(Object state) {
        int size = (Integer) state;
        Solution counter = new Solution();
        for (int i = 0; i < size; i++) {
            counter.add(i % 256, i / 256);
        }
        int expected = size / 256 - 1;
        for (int i = 0; i < 4; i++) {
            if (counter.count(0, 0) != expected) {
                throw new IllegalStateException("Wrong square count");
            }
        }
    }

    static int units(int size) {
        return 1;
    }
}
