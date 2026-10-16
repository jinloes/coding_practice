package com.jinloes.practice;

import java.util.Arrays;

/**
 * Scaling workload for Subsets with Duplicates. The input is n copies of one value, which has n + 1
 * distinct subsets of total length about n^2 / 2, so the work and output grow quadratically in n even
 * though the problem is exponential in general.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {128, 256, 512, 1024, 2048};

    private Workload() {}

    static Object prepare(int size) {
        int[] values = new int[size];
        Arrays.fill(values, 7);
        return values;
    }

    static void run(Object state) {
        int[] values = (int[]) state;
        if (Solution.subsets(values).size() != values.length + 1) {
            throw new IllegalStateException("Unexpected subset count");
        }
    }

    static int units(int size) {
        return 1;
    }
}
