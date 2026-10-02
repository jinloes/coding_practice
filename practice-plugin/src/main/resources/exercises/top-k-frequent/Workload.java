package com.jinloes.practice;

/**
 * Scaling workload for Top K Frequent Values. Input construction happens in prepare, outside the timed region,
 * so only the learner's own work is measured. Three values are frequent; the rest are mostly distinct.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        int[] ordered = new int[size];
        int heavy = size / 8;
        for (int i = 0; i < 3 * heavy; i++) {
            ordered[i] = i % 3;
        }
        for (int i = 3 * heavy; i < size; i++) {
            ordered[i] = i;
        }
        int[] numbers = new int[size];
        for (int i = 0; i < size; i++) {
            numbers[(int) ((i * 7919L) % size)] = ordered[i];
        }
        return numbers;
    }

    static void run(Object state) {
        int[] result = Solution.topKFrequent((int[]) state, 3);
        if (result.length != 3 || result[0] + result[1] + result[2] != 3) {
            throw new IllegalStateException("Probe must report the three heavy values");
        }
    }

    static int units(int size) {
        return 1;
    }
}
