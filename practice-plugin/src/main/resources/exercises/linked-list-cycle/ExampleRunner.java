package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the list values followed by the index the tail links back to (-1 for none), for example: [3, 2, 0, -4] 1";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.hasCycle(build(new int[]{1, 2, 3}, -1)))
                .as("Example 1: [1, 2, 3] has no cycle").isFalse();
        assertThat(Solution.hasCycle(build(new int[]{3, 2, 0, -4}, 1)))
                .as("Example 2: the tail links back to index 1").isTrue();
        assertThat(Solution.hasCycle(build(new int[]{5}, 0)))
                .as("Example 3: a single node linked to itself").isTrue();
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        int close = input.indexOf(']');
        if (!input.startsWith("[") || close < 0) {
            System.out.println("Could not read \"" + input + "\". " + USAGE);
            return;
        }
        int[] values;
        int position;
        try {
            values = parseInts(input.substring(1, close));
            position = Integer.parseInt(input.substring(close + 1).trim());
        } catch (NumberFormatException exception) {
            System.out.println("Could not read \"" + input + "\" as integers. " + USAGE);
            return;
        }
        if (values.length == 0 || position < -1 || position >= values.length) {
            System.out.println("The link index must be -1 or a valid position in a non-empty list. " + USAGE);
            return;
        }
        System.out.println("hasCycle = " + Solution.hasCycle(build(values, position)));
    }

    private static Solution.Node build(int[] values, int tailLinksTo) {
        Solution.Node[] nodes = new Solution.Node[values.length];
        for (int i = values.length - 1; i >= 0; i--) {
            nodes[i] = new Solution.Node(values[i], i + 1 < values.length ? nodes[i + 1] : null);
        }
        if (tailLinksTo >= 0) {
            nodes[values.length - 1].next = nodes[tailLinksTo];
        }
        return nodes[0];
    }

    private static int[] parseInts(String text) {
        String cleaned = text.replace(',', ' ').trim();
        if (cleaned.isEmpty()) {
            return new int[0];
        }
        String[] tokens = cleaned.split("\\s+");
        int[] values = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            values[i] = Integer.parseInt(tokens[i]);
        }
        return values;
    }
}
