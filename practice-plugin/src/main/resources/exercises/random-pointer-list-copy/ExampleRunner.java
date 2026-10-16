package com.jinloes.practice;

import java.util.IdentityHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: nodes as value@randomIndex (index from 0, or -1 for none) separated by spaces, for example: 3@1 7@-1 1@0";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.copyList(null)).as("Example 1: empty list").isNull();
        Solution.Node self = new Solution.Node(5);
        self.random = self;
        Solution.Node selfCopy = Solution.copyList(self);
        assertThat(selfCopy).as("Example 2: copy of a self-pointing node").isNotNull().isNotSameAs(self);
        assertThat(selfCopy.random).as("Example 2: the copy's random pointer").isSameAs(selfCopy);
        Solution.Node mixed = build(new int[]{3, 7, 1}, new int[]{1, -1, 0});
        Solution.Node mixedCopy = Solution.copyList(mixed);
        assertThat(describe(mixedCopy)).as("Example 3: copy of 3@1 7@-1 1@0").isEqualTo("3@1 7@-1 1@0");
        assertThat(mixedCopy).isNotSameAs(mixed);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        try {
            String[] tokens = input.split("\\s+");
            int[] values = new int[tokens.length];
            int[] randoms = new int[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                String[] parts = tokens[i].split("@");
                if (parts.length != 2) {
                    throw new NumberFormatException();
                }
                values[i] = Integer.parseInt(parts[0]);
                randoms[i] = Integer.parseInt(parts[1]);
                if (randoms[i] < -1 || randoms[i] >= tokens.length) {
                    System.out.println("Random index " + randoms[i] + " is out of range. " + USAGE);
                    return;
                }
            }
            System.out.println("copyList(" + input + ") = " + describe(Solution.copyList(build(values, randoms))));
        } catch (NumberFormatException exception) {
            System.out.println("Could not parse input. " + USAGE);
        }
    }

    private static Solution.Node build(int[] values, int[] randoms) {
        Solution.Node[] nodes = new Solution.Node[values.length];
        for (int i = 0; i < values.length; i++) {
            nodes[i] = new Solution.Node(values[i]);
            if (i > 0) {
                nodes[i - 1].next = nodes[i];
            }
        }
        for (int i = 0; i < values.length; i++) {
            nodes[i].random = randoms[i] < 0 ? null : nodes[randoms[i]];
        }
        return nodes[0];
    }

    private static String describe(Solution.Node head) {
        if (head == null) {
            return "null";
        }
        Map<Solution.Node, Integer> index = new IdentityHashMap<>();
        for (Solution.Node n = head; n != null; n = n.next) {
            index.put(n, index.size());
        }
        StringBuilder text = new StringBuilder();
        for (Solution.Node n = head; n != null; n = n.next) {
            String target = n.random == null ? "-1" : index.containsKey(n.random) ? String.valueOf(index.get(n.random)) : "?";
            text.append(text.length() == 0 ? "" : " ").append(n.value).append('@').append(target);
        }
        return text.toString();
    }
}
