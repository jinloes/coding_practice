package com.jinloes.practice;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: node count, then edges as a-b between nodes 1..count, for example: 4 1-2 2-3 3-4 4-1";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.cloneGraph(null)).as("Example 1: null input").isNull();
        Solution.GraphNode single = new Solution.GraphNode(7);
        Solution.GraphNode singleCopy = Solution.cloneGraph(single);
        assertThat(singleCopy).as("Example 2: copy of a single node").isNotNull().isNotSameAs(single);
        assertThat(singleCopy.value).isEqualTo(7);
        assertThat(singleCopy.neighbors).isEmpty();
        Solution.GraphNode triangle = build(3, new int[][]{{1, 2}, {2, 3}, {3, 1}});
        Solution.GraphNode triangleCopy = Solution.cloneGraph(triangle);
        assertThat(triangleCopy).as("Example 3: copy of a triangle").isNotNull().isNotSameAs(triangle);
        assertThat(describe(triangleCopy)).isEqualTo(describe(triangle));
        assertThat(triangleCopy.neighbors).doesNotContain(triangle.neighbors.toArray(new Solution.GraphNode[0]));
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        try {
            String[] tokens = input.split("\\s+");
            int count = Integer.parseInt(tokens[0]);
            if (count < 1) {
                System.out.println("Need at least one node. " + USAGE);
                return;
            }
            int[][] edges = new int[tokens.length - 1][];
            for (int i = 1; i < tokens.length; i++) {
                String[] pair = tokens[i].split("-");
                if (pair.length != 2) {
                    throw new NumberFormatException();
                }
                edges[i - 1] = new int[]{Integer.parseInt(pair[0]), Integer.parseInt(pair[1])};
                for (int end : edges[i - 1]) {
                    if (end < 1 || end > count) {
                        System.out.println("Edge " + tokens[i] + " is out of range. " + USAGE);
                        return;
                    }
                }
            }
            System.out.println("cloneGraph(node 1) = " + describe(Solution.cloneGraph(build(count, edges))));
        } catch (NumberFormatException exception) {
            System.out.println("Could not parse input. " + USAGE);
        }
    }

    private static Solution.GraphNode build(int count, int[][] edges) {
        Solution.GraphNode[] nodes = new Solution.GraphNode[count + 1];
        for (int i = 1; i <= count; i++) {
            nodes[i] = new Solution.GraphNode(i);
        }
        for (int[] edge : edges) {
            nodes[edge[0]].neighbors.add(nodes[edge[1]]);
            if (edge[0] != edge[1]) {
                nodes[edge[1]].neighbors.add(nodes[edge[0]]);
            }
        }
        return nodes[1];
    }

    private static String describe(Solution.GraphNode start) {
        if (start == null) {
            return "null";
        }
        TreeMap<Integer, String> lines = new TreeMap<>();
        Set<Solution.GraphNode> seen = Collections.newSetFromMap(new IdentityHashMap<>());
        ArrayDeque<Solution.GraphNode> pending = new ArrayDeque<>();
        seen.add(start);
        pending.add(start);
        while (!pending.isEmpty()) {
            Solution.GraphNode node = pending.remove();
            StringBuilder neighbors = new StringBuilder();
            for (Solution.GraphNode next : node.neighbors) {
                neighbors.append(neighbors.length() == 0 ? "" : ",").append(next.value);
                if (seen.add(next)) {
                    pending.add(next);
                }
            }
            lines.put(node.value, node.value + ":[" + neighbors + "]");
        }
        return String.join(" ", lines.values());
    }
}
