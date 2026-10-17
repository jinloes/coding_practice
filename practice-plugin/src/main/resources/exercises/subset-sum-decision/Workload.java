package com.jinloes.practice;

import java.util.Arrays;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {128, 256, 512, 1024, 2048};
    private Workload() {}
    static Object prepare(int size) { int[] a = new int[size]; Arrays.fill(a, 1); return new Object[]{a, size/2}; }
    static void run(Object state) { Object[] d=(Object[]) state; if (!Solution.hasSubsetSum((int[]) d[0], (Integer) d[1])) throw new IllegalStateException(); }
    static int units(int size) { return 1; }
}
