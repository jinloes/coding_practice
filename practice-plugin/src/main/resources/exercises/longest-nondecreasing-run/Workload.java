package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4000, 8000, 16000, 32000, 64000};
    private Workload() {}
    static Object prepare(int size) {
        int[] numbers = new int[size];
        for (int i = 0; i < size; i++) numbers[i] = i % 100;
        return numbers;
    }
    static void run(Object state) {
        if (Solution.longestRun((int[]) state) < 0) throw new IllegalStateException("negative");
    }
    static int units(int size) { return 1; }
}
