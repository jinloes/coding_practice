package com.jinloes.practice;

/**
 * Scaling workload. Input construction happens in prepare, outside the timed region.
 * The stream tracks a fixed 16 largest values while the input grows.
 */
final class Workload {
    static final String UNIT = "run";
    static final int[] SIZES = {32768, 65536, 131072, 262144, 524288};

    private Workload() {}

    private static int[] values(int count, long seed) {
        int[] values = new int[count];
        long state = seed;
        for (int i = 0; i < count; i++) {
            state = state * 6364136223846793005L + 1442695040888963407L;
            values[i] = (int) ((state >>> 33) % 1_000_000);
        }
        return values;
    }

    static Object prepare(int size) {
        return new int[][]{values(size / 2, 1), values(size / 2, 2), {16}};
    }

    static void run(Object state) {
        int[][] input = (int[][]) state;
        Solution stream = new Solution(input[2][0], input[0]);
        int last = -1;
        for (int value : input[1]) {
            last = stream.add(value);
        }
        if (last < 0) {
            throw new IllegalStateException("Answer out of range: " + last);
        }
    }

    static int units(int size) {
        return 1;
    }
}
