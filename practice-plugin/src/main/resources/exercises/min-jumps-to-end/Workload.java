package com.jinloes.practice;

/** Scaling workload for Min Jumps to End: every stone allows a hop of 2, repeated for stable timing. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private static final int REPEATS = 32;

    private Workload() {}

    static Object prepare(int size) {
        int[] reach = new int[size];
        java.util.Arrays.fill(reach, 2);
        return reach;
    }

    static void run(Object state) {
        int[] reach = (int[]) state;
        int expected = reach.length / 2;
        for (int repeat = 0; repeat < REPEATS; repeat++) {
            if (Solution.minJumps(reach) != expected) {
                throw new IllegalStateException("Unexpected hop count");
            }
        }
    }

    static int units(int size) {
        return REPEATS;
    }
}
