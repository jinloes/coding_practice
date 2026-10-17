package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {2000, 4000, 8000, 16000, 32000};
    private Workload() {}
    static Object prepare(int size) {
        int[] numbers = new int[size];
        for (int i = 0; i < size; i++) numbers[i] = i % 11 == 0 ? 3 : 1;
        return numbers;
    }
    static void run(Object state) { Solution.minSumOfLengths((int[]) state, 3); }
    static int units(int size) { return 1; }
}
