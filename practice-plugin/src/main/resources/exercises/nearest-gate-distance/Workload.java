package com.jinloes.practice;

/** Scaling workload for Nearest Gate Distance: one gate in the corner of a 64-column open grid. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private static final int COLS = 64;

    private Workload() {}

    static Object prepare(int size) {
        int[][] rooms = new int[size / COLS][COLS];
        for (int[] row : rooms) {
            java.util.Arrays.fill(row, Integer.MAX_VALUE);
        }
        rooms[0][0] = 0;
        return rooms;
    }

    static void run(Object state) {
        int[][] rooms = (int[][]) state;
        Solution.fillDistances(rooms);
        if (rooms[rooms.length - 1][COLS - 1] != rooms.length - 1 + COLS - 1) {
            throw new IllegalStateException("Unexpected distance");
        }
    }

    static int units(int size) {
        return 1;
    }
}
