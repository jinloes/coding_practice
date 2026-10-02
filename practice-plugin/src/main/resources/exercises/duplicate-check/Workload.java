package com.jinloes.practice;

/** Scaling workload for Duplicate Check; input construction happens outside the timed region. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {2048, 4096, 8192, 16384, 32768};

    private Workload() {}

    static Object prepare(int size) {
        int[] numbers = new int[size];
        for (int i = 0; i < size; i++) {
            numbers[i] = i * 3;
        }
        return numbers;
    }

    /** All values are distinct, so no solution can stop before reading everything. */
    static void run(Object state) {
        if (Solution.hasDuplicate((int[]) state)) {
            throw new IllegalStateException("Distinct input reported a duplicate");
        }
    }

    static int units(int size) {
        return 1;
    }
}
