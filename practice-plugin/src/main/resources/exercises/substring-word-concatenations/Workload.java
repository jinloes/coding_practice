package com.jinloes.practice;

final class Workload {
    static final String UNIT = "character";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private Workload() {}
    static Object prepare(int size) { StringBuilder text = new StringBuilder(size); String[] cycle = {"foo", "bar", "baz", "qux"}; for (int i = 0; text.length() < size; i++) text.append(cycle[i & 3]); return text.toString(); }
    static void run(Object state) { if (Solution.findStarts((String) state, new String[]{"foo", "bar", "baz"}).isEmpty()) throw new IllegalStateException(); }
    static int units(int size) { return 1; }
}
