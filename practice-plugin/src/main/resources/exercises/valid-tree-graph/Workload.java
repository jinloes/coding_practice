package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        int[][] edges = new int[size - 1][];
        for (int i = 0; i < edges.length; i++) {
            edges[i] = i % 2 == 0 ? new int[]{i, i + 1} : new int[]{i + 1, i};
        }
        return edges;
    }

    static void run(Object state) {
        int[][] edges = (int[][]) state;
        if (!Solution.isTree(edges.length + 1, edges)) {
            throw new IllegalStateException("A path graph is a tree");
        }
    }

    static int units(int size) { return 1; }
}
