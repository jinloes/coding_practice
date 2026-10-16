package com.jinloes.practice;

/**
 * Scaling workload. Input construction happens in prepare, outside the timed region.
 * The answer keeps a quarter of the points, so the result grows with the input size.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        int[][] points = new int[size][];
        long state = 7;
        for (int i = 0; i < size; i++) {
            state = state * 6364136223846793005L + 1442695040888963407L;
            int x = (int) ((state >>> 33) % 20001) - 10000;
            state = state * 6364136223846793005L + 1442695040888963407L;
            int y = (int) ((state >>> 33) % 20001) - 10000;
            points[i] = new int[]{x, y};
        }
        return points;
    }

    static void run(Object state) {
        int[][] points = (int[][]) state;
        int[][] near = Solution.closest(points, points.length / 4);
        if (near.length != points.length / 4) {
            throw new IllegalStateException("Expected " + points.length / 4 + " points but got " + near.length);
        }
    }

    static int units(int size) {
        return 1;
    }
}
