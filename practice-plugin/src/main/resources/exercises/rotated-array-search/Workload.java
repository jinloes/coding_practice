package com.jinloes.practice;

/** Scaling workload for Rotated Array Search; input construction happens outside the timed region. */
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

    /** One search per stored value, so the reported cost is the cost of a single search. */
    static void run(Object state) {
        int[] rotated = (int[]) state;
        for (int i = 0; i < rotated.length; i++) {
            int target = i * 2;
            int found = Solution.search(rotated, target);
            if (found < 0 || rotated[found] != target) {
                throw new IllegalStateException("Search missed " + target);
            }
        }
    }

    static int units(int size) {
        return size;
    }
}
