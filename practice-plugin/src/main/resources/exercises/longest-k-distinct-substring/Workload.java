package com.jinloes.practice;

final class Workload {
    static final String UNIT = "characters";
    static final int[] SIZES = {2048, 4096, 8192, 16384, 32768};
    private Workload() {}
    static Object prepare(int size) { StringBuilder text = new StringBuilder(size); for (int i = 0; i < size; i++) text.append((char) ('a' + (i % 8))); return text.toString(); }
    static void run(Object state) { Solution.longestAtMostKDistinct((String) state, 3); }
    static int units(int size) { return 1; }
}
