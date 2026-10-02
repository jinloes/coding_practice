package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: sorted lists separated by semicolons, for example: [1, 4, 5]; [1, 3, 4]; [2, 6]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(collect(Solution.mergeKLists(new Solution.Node[]{chain(1, 4, 5), chain(1, 3, 4), chain(2, 6)})))
                .as("Example 1: merge of three sorted lists").containsExactly(1, 1, 2, 3, 4, 4, 5, 6);
        assertThat(Solution.mergeKLists(new Solution.Node[0]))
                .as("Example 2: merge of no lists").isNull();
        assertThat(collect(Solution.mergeKLists(new Solution.Node[]{null, chain(-2, 7), null})))
                .as("Example 3: empty lists are skipped").containsExactly(-2, 7);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String[] parts = input.split(";");
        Solution.Node[] lists = new Solution.Node[parts.length];
        try {
            for (int i = 0; i < parts.length; i++) {
                int[] values = parseInts(parts[i]);
                for (int j = 1; j < values.length; j++) {
                    if (values[j] < values[j - 1]) {
                        System.out.println("Each list must be sorted in ascending order. " + USAGE);
                        return;
                    }
                }
                lists[i] = chain(values);
            }
        } catch (NumberFormatException exception) {
            System.out.println("Could not read \"" + input + "\" as lists of integers. " + USAGE);
            return;
        }
        System.out.println("mergeKLists(" + input + ") = " + collect(Solution.mergeKLists(lists)));
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
        String cleaned = text.replace('[', ' ').replace(']', ' ').replace(',', ' ').trim();
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
