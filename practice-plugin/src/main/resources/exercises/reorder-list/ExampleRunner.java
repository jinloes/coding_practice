package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the list values in order, for example: [1, 2, 3, 4]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution.Node even = chain(1, 2, 3, 4);
        Solution.reorder(even);
        assertThat(collect(even)).as("Example 1: reorder of [1, 2, 3, 4]").containsExactly(1, 4, 2, 3);
        Solution.Node odd = chain(1, 2, 3, 4, 5);
        Solution.reorder(odd);
        assertThat(collect(odd)).as("Example 2: reorder of [1, 2, 3, 4, 5]").containsExactly(1, 5, 2, 4, 3);
        Solution.Node single = chain(7);
        Solution.reorder(single);
        assertThat(collect(single)).as("Example 3: reorder of [7]").containsExactly(7);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        int[] values;
        try {
            values = parseInts(input);
        } catch (NumberFormatException exception) {
            System.out.println("Could not read \"" + input + "\" as integers. " + USAGE);
            return;
        }
        Solution.Node head = chain(values);
        Solution.reorder(head);
        System.out.println("reorder(" + input + ") -> " + collect(head));
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

    private static int[] parseInts(String input) {
        String cleaned = input.replace('[', ' ').replace(']', ' ').replace(',', ' ').trim();
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
