package com.jinloes.practice;

final class Workload {
    static final String UNIT = "operation";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private Workload() {}
    static Object prepare(int size) { return new int[]{size}; }
    static void run(Object state) {
        int size = ((int[]) state)[0];
        Solution table = new Solution(size * 2 + 1);
        for (int i = 0; i < size; i++) table.put("k" + i, "v" + i);
        for (int i = 0; i < size; i++) if (!table.get("k" + i).equals("v" + i)) throw new IllegalStateException();
        for (int i = 0; i < size; i += 2) table.remove("k" + i);
        if (table.size() != size / 2) throw new IllegalStateException();
    }
    static int units(int size) { return 1; }
}
