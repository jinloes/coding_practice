package com.jinloes.practice;
final class Workload {
    static final String UNIT = "window element";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private Workload() {}
    static Object prepare(int size) {
        int[] numbers = new int[size];
        for (int i = 0; i < size; i++) numbers[i] = (i * 31) % 1000 - 500;
        return numbers;
    }
    static void run(Object state) {
        int[] result = Solution.maxSlidingWindow((int[]) state, 128);
        long checksum = 0;
        for (int value : result) checksum += value;
        if (result.length == 0 || checksum == Long.MIN_VALUE) {
            throw new IllegalStateException("Unexpected result");
        }
    }
    static int units(int size) { return 1; }
}
