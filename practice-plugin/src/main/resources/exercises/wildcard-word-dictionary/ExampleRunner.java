package com.jinloes.practice;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: operations separated by commas, for example: add tree, add trap, matches tr.e, matches t.o";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        example1();
        example2();
        example3();
        System.out.println("Examples passed: 3");
    }

    private static void example1() {
        Solution dictionary = new Solution();
        dictionary.add("tree");
        dictionary.add("trap");
        dictionary.add("tone");
        assertThat(dictionary.matches("tr.e")).as("tr.e").isTrue();
        assertThat(dictionary.matches("t..e")).as("t..e").isTrue();
        assertThat(dictionary.matches("t.o")).as("t.o").isFalse();
    }

    private static void example2() {
        Solution dictionary = new Solution();
        dictionary.add("cat");
        assertThat(dictionary.matches("cat")).isTrue();
        assertThat(dictionary.matches("ca")).isFalse();
        assertThat(dictionary.matches("cats")).isFalse();
    }

    private static void example3() {
        Solution dictionary = new Solution();
        assertThat(dictionary.matches("a")).isFalse();
        assertThat(dictionary.matches(".")).isFalse();
    }

    private static void runInput(String input) {
        Solution dictionary = new Solution();
        for (String raw : input.split(",")) {
            String operation = raw.trim();
            if (operation.isEmpty()) {
                continue;
            }
            String[] parts = operation.split("\\s+");
            if (parts.length != 2 || !parts[1].matches("[a-z.]+")) {
                System.out.println("'" + operation + "' needs an operation and a lowercase word or pattern. " + USAGE);
                return;
            }
            switch (parts[0]) {
                case "add" -> {
                    if (parts[1].contains(".")) {
                        System.out.println("Words added to the dictionary cannot contain '.'. " + USAGE);
                        return;
                    }
                    dictionary.add(parts[1]);
                    System.out.println("add(" + parts[1] + ")");
                }
                case "matches" -> System.out.println("matches(" + parts[1] + ") = " + dictionary.matches(parts[1]));
                default -> {
                    System.out.println("Unknown operation '" + parts[0] + "'. " + USAGE);
                    return;
                }
            }
        }
    }


}
