package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the task letters A-Z with no spaces, then the cooldown, for example: AAABBB 2";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.minSlots("AAABBB", 2)).as("Example 1").isEqualTo(8);
        assertThat(Solution.minSlots("ABCD", 3)).as("Example 2").isEqualTo(4);
        assertThat(Solution.minSlots("AAAB", 2)).as("Example 3").isEqualTo(7);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String tasks;
        int cooldown;
        try {
            String[] parts = input.split("[\\s,]+");
            if (parts.length != 2 || !parts[0].matches("[A-Z]+")) {
                throw new IllegalArgumentException("expected letters and a cooldown");
            }
            tasks = parts[0];
            cooldown = Integer.parseInt(parts[1]);
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\". " + USAGE);
            return;
        }
        try {
            System.out.println("minSlots(" + tasks + ", " + cooldown + ") = " + Solution.minSlots(tasks, cooldown));
        } catch (IllegalArgumentException exception) {
            System.out.println("Rejected: " + exception.getMessage());
        }
    }
}
