package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private Workload() {}
    static Object prepare(int size) { List<Integer> h = new ArrayList<>(); for (int i = 0; i < size; i++) h.add((i * 31) % 997); return h; }
    static void run(Object state) { long sum = 0; for (int i = 0; i < 64; i++) sum += Solution.sunsetViews((List<Integer>) state).size(); if (sum == 0) throw new IllegalStateException("empty"); }
    static int units(int size) { return 1; }
}
