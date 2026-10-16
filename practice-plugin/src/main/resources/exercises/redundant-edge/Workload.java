package com.jinloes.practice;

/** Scaling workload for Redundant Edge: a path of "size" nodes closed into a ring by the last edge. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        int[][] edges = new int[size][];
        for (int i = 1; i < size; i++) {
            edges[i - 1] = new int[]{i, i + 1};
        }
        edges[size - 1] = new int[]{size, 1};
        return edges;
    }

    static void run(Object state) {
        int[][] edges = (int[][]) state;
        int[] answer = Solution.redundantEdge(edges);
        if (answer[0] != edges.length || answer[1] != 1) {
            throw new IllegalStateException("Unexpected redundant edge");
        }
    }

    static int units(int size) {
        return 1;
    }
}
