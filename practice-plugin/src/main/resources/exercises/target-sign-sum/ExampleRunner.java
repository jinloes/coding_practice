package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the array values, then the target, for example: [1, 1, 1, 1, 1] 3";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.countAssignments(new int[]{1, 1, 1, 1, 1}, 3)).as("countAssignments([1, 1, 1, 1, 1], 3)").isEqualTo(5);
        assertThat(Solution.countAssignments(new int[]{2, 3}, 7)).as("countAssignments([2, 3], 7)").isEqualTo(0);
        assertThat(Solution.countAssignments(new int[]{0, 0, 1}, 1)).as("countAssignments([0, 0, 1], 1)").isEqualTo(4);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        int[] values;
        int target;
        try {
            java.util.regex.Matcher matcher =
                    java.util.regex.Pattern.compile("\\s*\\[([^\\]]*)\\]\\s*(-?\\d+)\\s*").matcher(input);
            if (!matcher.matches()) {
                throw new IllegalArgumentException("bad shape");
            }
            values = parseInts(matcher.group(1));
            target = Integer.parseInt(matcher.group(2));
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\". " + USAGE);
            return;
        }
        System.out.println("countAssignments(" + Arrays.toString(values) + ", " + target + ")) = " + Solution.countAssignments(values, target));
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
