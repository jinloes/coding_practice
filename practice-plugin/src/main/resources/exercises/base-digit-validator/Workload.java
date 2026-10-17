package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {2048, 4096, 8192, 16384, 32768};
    private Workload() {}
    static Object prepare(int size) {
        StringBuilder b = new StringBuilder(size);
        for (int j = 0; j < size - 1; j++) b.append((char) ('0' + (j % 10)));
        b.append('z');
        return b.toString();
    }
    static void run(Object state) { if (Solution.isValidForBase((String) state, 10)) throw new IllegalStateException("accepted invalid number"); }
    static int units(int size) { return 1; }
}
