package com.jinloes.practice;

final class Workload {
    static final String UNIT = "street";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private Workload() {}
    static Object prepare(int size) { int[] a = new int[size]; for (int i=0;i<size;i++) a[i] = (i * 37) & 1023; return a; }
    static void run(Object state) { if (Solution.maxProfit((int[]) state) < 0) throw new IllegalStateException("negative profit"); }
    static int units(int size) { return 1; }
}
