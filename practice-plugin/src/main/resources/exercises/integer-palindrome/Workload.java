package com.jinloes.practice;

final class Workload {
    static final String UNIT = "batch";
    static final int[] SIZES = {1024, 2048, 4096, 8192, 16384};
    private Workload() {}
    static Object prepare(int size) { return size; }
    static void run(Object state) {
        int size = (Integer) state; int hits = 0;
        for (int j = 0; j < 20000; j++) if (Solution.isPalindrome(1_000_000_001 + ((size + j) % 8998))) hits++;
        if (hits < 0) throw new IllegalStateException("unreachable");
    }
    static int units(int size) { return 1; }
}
