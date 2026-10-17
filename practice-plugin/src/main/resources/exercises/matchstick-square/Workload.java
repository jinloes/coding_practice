package com.jinloes.practice;

final class Workload {
    static final String UNIT = "puzzle";
    static final int[] SIZES = {1024, 2048, 4096, 8192, 16384};
    private Workload() {}
    static Object prepare(int size) { return size; }
    static void run(Object state) {
        int calls = (Integer) state;
        for (int i = 0; i < calls; i++) if (!Solution.canFormSquare(new int[]{1,1,2,2,2})) throw new IllegalStateException();
    }
    static int units(int size) { return 1; }
}
