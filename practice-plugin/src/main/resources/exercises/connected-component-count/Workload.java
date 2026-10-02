package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        int[][] edges = new int[size / 2][];
        for (int i = 0; i < edges.length; i++) {
            edges[i] = new int[]{2 * i + 1, 2 * i};
        }
        return edges;
    }

    static void run(Object state) {
        int[][] edges = (int[][]) state;
        int components = Solution.countComponents(edges.length * 2, edges);
        if (components != edges.length) {
            throw new IllegalStateException("Expected " + edges.length + " components but found " + components);
        }
    }

    static int units(int size) { return 1; }
}
