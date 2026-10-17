package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {512, 1024, 2048, 4096, 8192};
    private Workload() {}
    static Object prepare(int size) { return size; }
    static void run(Object state) { int calls = (Integer) state; for (int i = 0; i < calls; i++) if (Solution.combinations(8, 4).size() != 70) throw new IllegalStateException(); }
    static int units(int size) { return 1; }
}
