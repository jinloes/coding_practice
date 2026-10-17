package com.jinloes.practice;

final class Workload {
    static final String UNIT = "player";
    static final int[] SIZES = {2048, 4096, 8192, 16384, 32768};
    private Workload() {}
    static Object prepare(int size) { Solution board = new Solution(); for (int i = 0; i < size; i++) board.addScore(i, (i * 37) % 1000); return new Object[]{board, size}; }
    static void run(Object state) { Object[] data = (Object[]) state; Solution board = (Solution) data[0]; int size = (Integer) data[1]; if (board.top(size / 2) <= 0) throw new IllegalStateException(); }
    static int units(int size) { return 1; }
}
