package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4000, 8000, 16000, 32000, 64000};
    private Workload() {}
    static Object prepare(int size) { int[] a = new int[size]; for (int i = 0; i < size; i++) a[i] = i % 2 == 0 ? i : -i; return a; }
    static void run(Object state) { Solution.longestZigZag((int[]) state); }
    static int units(int size) { return 1; }
}
