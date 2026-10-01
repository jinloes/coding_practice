package com.jinloes.practice;
final class Workload {
    static final String UNIT = "course edge";
    static final int[] SIZES = {512, 1024, 2048, 4096, 8192};
    private Workload() {}
    static Object prepare(int size) {
        int[][] prerequisites = new int[size][2];
        for (int i = 0; i < size; i++) prerequisites[i] = new int[]{i + 1, i};
        return prerequisites;
    }
    static void run(Object state) {
        int[][] prerequisites = (int[][]) state;
        if (!Solution.canFinish(prerequisites.length + 1, prerequisites)) throw new IllegalStateException("Unexpected cycle");
    }
    static int units(int size) { return 1; }
}
