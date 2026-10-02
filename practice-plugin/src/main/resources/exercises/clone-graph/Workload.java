package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {1024, 2048, 4096, 8192, 16384};

    private Workload() {}

    static Object prepare(int size) {
        Solution.GraphNode[] nodes = new Solution.GraphNode[size];
        for (int i = 0; i < size; i++) {
            nodes[i] = new Solution.GraphNode(i);
        }
        for (int i = 0; i < size; i++) {
            Solution.GraphNode next = nodes[(i + 1) % size];
            Solution.GraphNode skip = nodes[(i + 7) % size];
            nodes[i].neighbors.add(next);
            next.neighbors.add(nodes[i]);
            nodes[i].neighbors.add(skip);
            skip.neighbors.add(nodes[i]);
        }
        return nodes[0];
    }

    static void run(Object state) {
        Solution.GraphNode original = (Solution.GraphNode) state;
        Solution.GraphNode copy = Solution.cloneGraph(original);
        if (copy == null || copy == original || copy.value != original.value
                || copy.neighbors.size() != original.neighbors.size()
                || copy.neighbors.get(0) == original.neighbors.get(0)) {
            throw new IllegalStateException("Clone is not a separate copy of the graph");
        }
    }

    static int units(int size) { return 1; }
}
