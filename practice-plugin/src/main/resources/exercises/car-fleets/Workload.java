package com.jinloes.practice;

/** Scaling workload for Car Fleets; n is the number of cars, placed at shuffled distinct positions. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        int[] positions = new int[size];
        int[] speeds = new int[size];
        for (int i = 0; i < size; i++) {
            positions[i] = (int) ((i * 7919L) % size);
            speeds[i] = 1 + (int) ((i * 31L) % 17);
        }
        return new int[][] {positions, speeds, {2 * size}};
    }

    static void run(Object state) {
        int[][] data = (int[][]) state;
        int fleets = Solution.countFleets(data[2][0], data[0], data[1]);
        if (fleets < 1 || fleets > data[0].length) {
            throw new IllegalStateException("Unexpected fleet count " + fleets);
        }
    }

    static int units(int size) {
        return 1;
    }
}
