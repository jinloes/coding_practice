package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the cards, a semicolon, then the group size, for example: [4, 5, 6, 9, 10, 11]; 3";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        check(new int[]{4, 5, 6, 9, 10, 11}, 3, true);
        check(new int[]{1, 2, 3, 5}, 2, false);
        check(new int[]{8}, 1, true);
        System.out.println("Examples passed: 3");
    }

    private static void check(int[] cards, int groupSize, boolean expected) {
        int[] copy = cards.clone();
        String call = "canGroup(%s, %d)".formatted(Arrays.toString(cards), groupSize);
        assertThat(Solution.canGroup(cards, groupSize)).as(call).isEqualTo(expected);
        assertThat(cards).as(call + " must not modify its input").containsExactly(copy);
    }

    private static void runInput(String input) {
        int[] cards;
        int groupSize;
        try {
            String[] parts = input.split(";", -1);
            if (parts.length != 2) {
                throw new IllegalArgumentException("cards and size expected");
            }
            cards = parseInts(parts[0]);
            groupSize = Integer.parseInt(parts[1].trim());
            if (groupSize < 1) {
                throw new IllegalArgumentException("size must be positive");
            }
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\" as cards and a group size. " + USAGE);
            return;
        }
        System.out.println("canGroup(" + Arrays.toString(cards) + ", " + groupSize + ") = "
                + Solution.canGroup(cards, groupSize));
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
