package com.jinloes.practice;

final class Workload {
    static final String UNIT = "task";
    static final int[] SIZES = {1024, 2048, 4096, 8192, 16384};
    private Workload() {}
    static Object prepare(int size) { int[][] tasks = new int[size][2]; for (int i = 0; i < size; i++) { tasks[i][0] = (i * 37) % (size / 2 + 1); tasks[i][1] = 1 + (i * 53) % 97; } return tasks; }
    static void run(Object state) { int[] order = Solution.processingOrder((int[][]) state); if (order.length != ((int[][]) state).length) throw new IllegalStateException("Missing tasks"); }
    static int units(int size) { return 1; }
}
