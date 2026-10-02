package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the list values followed by k, for example: [1, 2, 3, 4, 5] 2";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(collect(Solution.removeFromEnd(chain(1, 2, 3, 4, 5), 2)))
                .as("Example 1: removing the 2nd node from the end").containsExactly(1, 2, 3, 5);
        assertThat(Solution.removeFromEnd(chain(7), 1))
                .as("Example 2: removing the only node returns null").isNull();
        assertThat(collect(Solution.removeFromEnd(chain(1, 2), 2)))
                .as("Example 3: removing the head").containsExactly(2);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        int close = input.indexOf(']');
        if (!input.startsWith("[") || close < 0) {
            System.out.println("Could not read \"" + input + "\". " + USAGE);
            return;
        }
        int[] values;
        int k;
        try {
            values = parseInts(input.substring(1, close));
            k = Integer.parseInt(input.substring(close + 1).trim());
        } catch (NumberFormatException exception) {
            System.out.println("Could not read \"" + input + "\" as integers. " + USAGE);
            return;
        }
        if (k < 1 || k > values.length) {
            System.out.println("k must be between 1 and the list length. " + USAGE);
            return;
        }
        System.out.println("removeFromEnd(" + input + ") = " + collect(Solution.removeFromEnd(chain(values), k)));
    }

    private static Solution.Node chain(int... values) {
        Solution.Node head = null;
        for (int i = values.length - 1; i >= 0; i--) {
            head = new Solution.Node(values[i], head);
        }
        return head;
    }

    private static List<Integer> collect(Solution.Node head) {
        List<Integer> values = new ArrayList<>();
        for (Solution.Node node = head; node != null && values.size() <= 1000; node = node.next) {
            values.add(node.value);
        }
        return values;
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
