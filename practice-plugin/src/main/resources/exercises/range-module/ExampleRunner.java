package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: op1, op2, ... where ops are add l r | remove l r | query l r, for example: add 10 20, remove 14 16, query 10 14, query 13 15, query 16 17";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution r1 = new Solution();
        r1.addRange(10, 20);
        r1.removeRange(14, 16);
        assertThat(r1.queryRange(10, 14)).as("Example 1 left").isTrue();
        assertThat(r1.queryRange(13, 15)).as("Example 1 gap").isFalse();
        assertThat(r1.queryRange(16, 17)).as("Example 1 right").isTrue();

        Solution r2 = new Solution();
        r2.addRange(1, 5);
        r2.addRange(5, 10);
        assertThat(r2.queryRange(2, 9)).as("Example 2 merged").isTrue();

        Solution r3 = new Solution();
        r3.addRange(1, 5);
        r3.addRange(7, 12);
        r3.removeRange(3, 9);
        assertThat(r3.queryRange(1, 3)).as("Example 3 left").isTrue();
        assertThat(r3.queryRange(9, 12)).as("Example 3 right").isTrue();
        assertThat(r3.queryRange(3, 4)).as("Example 3 removed").isFalse();
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        if (input.isEmpty()) {
            System.out.println("Provide at least one operation. " + USAGE);
            return;
        }
        Solution rm = new Solution();
        List<String> outputs = new ArrayList<>();
        try {
            for (String raw : input.split(",")) {
                String op = raw.trim();
                if (op.isEmpty()) {
                    continue;
                }
                String[] parts = op.split("\\s+");
                if (parts.length != 3) {
                    System.out.println("Invalid op '" + op + "'. " + USAGE);
                    return;
                }
                int left = Integer.parseInt(parts[1]);
                int right = Integer.parseInt(parts[2]);
                switch (parts[0]) {
                    case "add" -> rm.addRange(left, right);
                    case "remove" -> rm.removeRange(left, right);
                    case "query" -> outputs.add("query=" + rm.queryRange(left, right));
                    default -> {
                        System.out.println("Unknown op '" + parts[0] + "'. " + USAGE);
                        return;
                    }
                }
            }
        } catch (NumberFormatException exception) {
            System.out.println("Invalid integer argument. " + USAGE);
            return;
        }
        System.out.println(String.join(", ", outputs));
    }
}
