package com.jinloes.practice;

import java.util.List;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {2048, 4096, 8192, 16384, 32768};
    private static final List<Solution.Score> RECORDS = List.of(
            new Solution.Score("Ada", 90), new Solution.Score("Ada", 95), new Solution.Score("Ada", 100),
            new Solution.Score("Bob", 100), new Solution.Score("Bob", 70), new Solution.Score("Bob", 60),
            new Solution.Score("Cid", 80), new Solution.Score("Cid", 81), new Solution.Score("Cid", 82));
    private Workload() {}
    static Object prepare(int size) { return size; }
    static void run(Object state) {
        int calls = (Integer) state;
        for (int i = 0; i < calls; i++) if (!Solution.bestThreeAverage(RECORDS).equals("Ada")) throw new IllegalStateException();
    }
    static int units(int size) { return 1; }
}
