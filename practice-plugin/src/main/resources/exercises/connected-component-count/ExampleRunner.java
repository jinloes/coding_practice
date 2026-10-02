package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: node count, then undirected edges as a-b between nodes 0..count-1, for example: 5 0-1 1-2 3-4";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.countComponents(4, new int[0][2])).as("Example 1").isEqualTo(4);
        assertThat(Solution.countComponents(5, new int[][]{{0, 1}, {1, 2}, {3, 4}})).as("Example 2").isEqualTo(2);
        assertThat(Solution.countComponents(3, new int[][]{{0, 1}, {1, 2}, {2, 0}})).as("Example 3").isEqualTo(1);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        try {
            String[] tokens = input.split("\\s+");
            int count = Integer.parseInt(tokens[0]);
            if (count < 0) {
                System.out.println("Node count is too small. " + USAGE);
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
                    if (end < 0 || end >= count) {
                        System.out.println("Edge " + tokens[i] + " is out of range. " + USAGE);
                        return;
                    }
                }
            }
            System.out.println("countComponents(" + count + ", " + Arrays.deepToString(edges) + ") = "
                    + Solution.countComponents(count, edges));
        } catch (NumberFormatException exception) {
            System.out.println("Could not parse input. " + USAGE);
        }
    }
}
