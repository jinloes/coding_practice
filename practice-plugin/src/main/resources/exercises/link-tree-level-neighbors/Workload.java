package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private Workload() {}
    static Object prepare(int size) { return build(size, 1); }
    static void run(Object state) { Solution.Node root = (Solution.Node) state; for (int i = 0; i < 8; i++) Solution.connect(root); if (root.next != null) throw new IllegalStateException("bad root"); }
    static int units(int size) { return 1; }
    private static Solution.Node build(int remaining, int value) { if (remaining <= 0) return null; int left = (remaining - 1) / 2; int right = remaining - 1 - left; return new Solution.Node(value, build(left, value * 2), build(right, value * 2 + 1)); }
}
