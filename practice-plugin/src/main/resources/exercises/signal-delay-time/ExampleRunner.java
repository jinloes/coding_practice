package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: node count, source node, then links as from>to:delay, for example: 4 1 1>2:4 1>3:1 3>2:2 2>4:5";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.signalDelay(new int[][]{{1, 2, 4}, {1, 3, 1}, {3, 2, 2}, {2, 4, 5}}, 4, 1))
                .as("Example 1").isEqualTo(8);
        assertThat(Solution.signalDelay(new int[][]{{1, 2, 3}}, 3, 1)).as("Example 2").isEqualTo(-1);
        assertThat(Solution.signalDelay(new int[0][], 1, 1)).as("Example 3").isEqualTo(0);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        try {
            String[] tokens = input.split("\\s+");
            int nodes = Integer.parseInt(tokens[0]);
            int source = Integer.parseInt(tokens[1]);
            if (nodes < 1 || source < 1 || source > nodes) {
                throw new IllegalArgumentException();
            }
            int[][] links = new int[tokens.length - 2][];
            for (int i = 2; i < tokens.length; i++) {
                String[] parts = tokens[i].split("[>:]");
                if (parts.length != 3) {
                    throw new IllegalArgumentException();
                }
                int from = Integer.parseInt(parts[0]);
                int to = Integer.parseInt(parts[1]);
                int delay = Integer.parseInt(parts[2]);
                if (from < 1 || from > nodes || to < 1 || to > nodes || delay < 0) {
                    throw new IllegalArgumentException();
                }
                links[i - 2] = new int[]{from, to, delay};
            }
            System.out.println("signalDelay(" + nodes + " nodes, source " + source + ") = "
                    + Solution.signalDelay(links, nodes, source));
        } catch (RuntimeException exception) {
            System.out.println("Could not parse input. " + USAGE);
        }
    }
}
