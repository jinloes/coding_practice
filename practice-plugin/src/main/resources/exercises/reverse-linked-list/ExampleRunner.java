package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the list values in order, for example: [1, 2, 3]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertValues(Solution.reverse(
                new Solution.Node(1, new Solution.Node(2, new Solution.Node(3, null)))), 3, 2, 1);
        Solution.Node single = new Solution.Node(8, null);
        assertThat(Solution.reverse(single))
                .as("Example 2: reversing a single node returns that same node")
                .isSameAs(single);
        assertThat(Solution.reverse(null)).as("Example 3: reversing null returns null").isNull();
        System.out.println("Examples passed: 3");
    }

    private static void assertValues(Solution.Node head, int... expected) {
        List<Integer> actual = new ArrayList<>();
        for (Solution.Node node = head; node != null && actual.size() <= expected.length; node = node.next) {
            actual.add(node.value);
        }
        List<Integer> wanted = new ArrayList<>();
        for (int value : expected) {
            wanted.add(value);
        }
        assertThat(actual).as("Example 1: reversed list values").isEqualTo(wanted);
    }

    private static void runInput(String input) {
        int[] values;
        try {
            values = parseInts(input);
        } catch (NumberFormatException exception) {
            System.out.println("Could not read \"" + input + "\" as integers. " + USAGE);
            return;
        }
        Solution.Node head = null;
        for (int i = values.length - 1; i >= 0; i--) {
            head = new Solution.Node(values[i], head);
        }
        System.out.println("reverse(" + render(values) + ") = " + render(collect(Solution.reverse(head))));
    }

    private static List<Integer> collect(Solution.Node head) {
        List<Integer> values = new ArrayList<>();
        for (Solution.Node node = head; node != null && values.size() <= 1000; node = node.next) {
            values.add(node.value);
        }
        return values;
    }

    private static String render(List<Integer> values) {
        return values.toString();
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
