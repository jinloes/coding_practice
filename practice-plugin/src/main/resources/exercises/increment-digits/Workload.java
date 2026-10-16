package com.jinloes.practice;

/** Scaling workload for Increment Digits. n is the number of digits, all nines, so the carry runs the whole way. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        int[] digits = new int[size];
        java.util.Arrays.fill(digits, 9);
        return digits;
    }

    static void run(Object state) {
        int[] digits = (int[]) state;
        int[] result = Solution.increment(digits);
        if (result.length != digits.length + 1 || result[0] != 1 || result[digits.length] != 0) {
            throw new IllegalStateException("Wrong carry result");
        }
    }

    static int units(int size) {
        return 1;
    }
}
