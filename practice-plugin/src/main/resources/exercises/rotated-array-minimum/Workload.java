package com.jinloes.practice;

/** Scaling workload for Rotated Array Minimum; input construction happens outside the timed region. */
final class Workload {
    static final String UNIT = "search";
    static final int[] SIZES = {32768, 65536, 131072, 262144, 524288};

    private Workload() {}

    static Object prepare(int size) {
        int[] rotated = new int[size];
        int shift = size / 3;
        for (int i = 0; i < size; i++) {
            rotated[i] = ((i + shift) % size) * 2;
        }
        return rotated;
    }

    /** Repeats the search so the reported cost is the cost of a single call. */
    static void run(Object state) {
        int[] rotated = (int[]) state;
        for (int i = 0; i < rotated.length; i++) {
            if (Solution.findMinimum(rotated) != 0) {
                throw new IllegalStateException("Unexpected minimum");
            }
        }
    }

    static int units(int size) {
        return size;
    }
}
