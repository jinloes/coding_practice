package com.jinloes.practice;

/**
 * Scaling workload for Signal Delay Time: a chain of {@code size} nodes where every node links to
 * the next (delay 1) and to the one after (delay 3), so the slowest node is size - 1 away.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        int[][] links = new int[2 * (size - 1)][];
        int next = 0;
        for (int node = 1; node < size; node++) {
            links[next++] = new int[]{node, node + 1, 1};
            links[next++] = new int[]{node, Math.min(size, node + 2), 3};
        }
        return links;
    }

    static void run(Object state) {
        int[][] links = (int[][]) state;
        int size = links.length / 2 + 1;
        if (Solution.signalDelay(links, size, 1) != size - 1) {
            throw new IllegalStateException("Unexpected slowest arrival");
        }
    }

    static int units(int size) {
        return 1;
    }
}
