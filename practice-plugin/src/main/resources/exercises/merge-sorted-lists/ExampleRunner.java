package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the two sorted lists separated by a semicolon, for example: [1, 2, 4]; [1, 3, 4]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(collect(Solution.merge(list(1, 2, 4), list(1, 3, 4))))
                .as("Example 1: merge([1, 2, 4], [1, 3, 4])")
                .containsExactly(1, 1, 2, 3, 4, 4);
        Solution.Node only = new Solution.Node(0, null);
        assertThat(Solution.merge(null, only))
                .as("Example 2: merging an empty list returns the other list unchanged")
                .isSameAs(only);
        assertThat(Solution.merge(null, null))
                .as("Example 3: merging two empty lists returns null").isNull();
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String[] parts = input.split(";", -1);
        if (parts.length != 2) {
            System.out.println("Expected two lists separated by one semicolon but read "
                    + parts.length + " part(s). " + USAGE);
            return;
        }
        int[] left;
        int[] right;
        try {
            left = parseInts(parts[0]);
            right = parseInts(parts[1]);
        } catch (NumberFormatException exception) {
            System.out.println("Could not read \"" + input + "\" as two lists of integers. " + USAGE);
            return;
        }
        if (!isSorted(left) || !isSorted(right)) {
            System.out.println("The contract requires both lists to be sorted in nondecreasing order. " + USAGE);
            return;
        }
        System.out.println("merge(" + render(left) + ", " + render(right) + ") = "
                + collect(Solution.merge(list(left), list(right))));
    }

    private static boolean isSorted(int[] values) {
        for (int index = 1; index < values.length; index++) {
            if (values[index] < values[index - 1]) {
                return false;
            }
        }
        return true;
    }

    private static Solution.Node list(int... values) {
        Solution.Node head = null;
        for (int index = values.length - 1; index >= 0; index--) {
            head = new Solution.Node(values[index], head);
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

    private static String render(int[] values) {
        List<Integer> boxed = new ArrayList<>();
        for (int value : values) {
            boxed.add(value);
        }
        return boxed.toString();
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
