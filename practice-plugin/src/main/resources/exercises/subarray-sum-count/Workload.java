package com.jinloes.practice;

final class Workload {
    static final String UNIT = "subarray count";
    static final int[] SIZES = {1024, 2048, 4096, 8192, 16384};
    private Workload() {}
    static Object prepare(int size) {
        int[] numbers = new int[size];
        for (int i = 0; i < size; i++) numbers[i] = (i % 7) - 3;
        return numbers;
    }
    static void run(Object state) {
        int[] numbers = (int[]) state;
        int result = Solution.countSubarrays(numbers, 0);
        if (result < 0) throw new IllegalStateException("Unexpected count");
    }
    static int units(int size) { return 1; }
}
