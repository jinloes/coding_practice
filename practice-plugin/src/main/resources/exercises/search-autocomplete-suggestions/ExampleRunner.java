package com.jinloes.practice;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: product1, product2 | query, for example: mobile, mouse, moneypot, monitor, mousepad | mouse";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.suggest(new String[] {"mobile", "mouse", "moneypot", "monitor", "mousepad"}, "mouse"))
                .as("Example 1")
                .containsExactly(
                        List.of("mobile", "moneypot", "monitor"),
                        List.of("mobile", "moneypot", "monitor"),
                        List.of("mouse", "mousepad"),
                        List.of("mouse", "mousepad"),
                        List.of("mouse", "mousepad"));
        assertThat(Solution.suggest(new String[] {"haven"}, "haven"))
                .as("Example 2")
                .containsExactly(
                        List.of("haven"),
                        List.of("haven"),
                        List.of("haven"),
                        List.of("haven"),
                        List.of("haven"));
        assertThat(Solution.suggest(new String[] {"bag", "ball"}, "cat"))
                .as("Example 3")
                .containsExactly(List.of(), List.of(), List.of());
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String[] parts = input.split("\\|", -1);
        if (parts.length != 2) {
            System.out.println("Separate products and query with '|'. " + USAGE);
            return;
        }
        String[] products = Arrays.stream(parts[0].split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
        String query = parts[1].trim();
        if (query.isEmpty()) {
            System.out.println("Provide a non-empty query after '|'. " + USAGE);
            return;
        }
        System.out.println("suggest = " + Solution.suggest(products, query));
    }
}
