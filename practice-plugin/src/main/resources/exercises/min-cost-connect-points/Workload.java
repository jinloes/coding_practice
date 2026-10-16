package com.jinloes.practice;

/**
 * Scaling workload for Min Cost to Connect Points: {@code size} collinear points, 3 apart, in a
 * scrambled order. The tree cost is known and every point still has to be compared with the rest.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {256, 512, 1024, 2048, 4096};

    private Workload() {}

    static Object prepare(int size) {
        int[][] points = new int[size][];
        for (int i = 0; i < size; i++) {
            points[i] = new int[]{3 * ((i * 37) % size), 0};
        }
        return points;
    }

    static void run(Object state) {
        int[][] points = (int[][]) state;
        if (Solution.minimumCost(points) != 3 * (points.length - 1)) {
            throw new IllegalStateException("Unexpected tree cost");
        }
    }

    static int units(int size) {
        return 1;
    }
}
