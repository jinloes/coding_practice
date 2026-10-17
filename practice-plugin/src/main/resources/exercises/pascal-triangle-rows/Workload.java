package com.jinloes.practice;

import java.util.List;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {64, 128, 256, 512, 1024};
    private Workload() {}
    static Object prepare(int size) { return size; }
    static void run(Object state) { int size=(Integer)state; List<List<Integer>> t=Solution.rows(size); if(t.size()!=size || t.get(size-1).size()!=size) throw new IllegalStateException("bad triangle"); }
    static int units(int size) { return 1; }
}
