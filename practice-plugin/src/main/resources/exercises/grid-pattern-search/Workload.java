package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {40, 80, 160, 320, 640};
    private Workload() {}
    static Object prepare(int size) {
        String[] grid = new String[size];
        StringBuilder row = new StringBuilder(size);
        for (int c = 0; c < size; c++) row.append((char) ('0' + (c % 10)));
        String text = row.toString();
        for (int r = 0; r < size; r++) grid[r] = text;
        return grid;
    }
    static void run(Object state) {
        if (Solution.containsPattern((String[]) state, new String[]{"xxx", "yyy", "zzz"}))
            throw new IllegalStateException("unexpected");
    }
    static int units(int size) { return 1; }
}
