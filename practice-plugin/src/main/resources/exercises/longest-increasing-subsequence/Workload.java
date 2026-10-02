package com.jinloes.practice;

/**
 * Scaling workload for Longest Increasing Subsequence. Pseudo-random input is built in prepare,
 * outside the timed region.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {16384, 32768, 65536, 131072, 262144};

    private Workload() {}

    static Object prepare(int size) {
        int[] numbers = new int[size];
        long seed = 987654321L;
        for (int i = 0; i < size; i++) {
            seed = (seed * 6364136223846793005L + 1442695040888963407L);
            numbers[i] = (int) (seed >>> 33);
        }
        return numbers;
    }

    static void run(Object state) {
        int[] numbers = (int[]) state;
        int result = Solution.lengthOfLis(numbers);
        if (result < 1 || result > numbers.length) {
            throw new IllegalStateException("Unexpected subsequence length");
        }
    }

    static int units(int size) {
        return 1;
    }
}
