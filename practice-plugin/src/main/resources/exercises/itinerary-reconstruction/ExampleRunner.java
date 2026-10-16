package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: start airport, then tickets as FROM>TO (uppercase codes), for example: SEA SEA>PDX PDX>SEA SEA>LAX";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.buildItinerary(tickets("SEA>PDX", "PDX>SEA", "SEA>LAX"), "SEA"))
                .as("Example 1").containsExactly("SEA", "PDX", "SEA", "LAX");
        assertThat(Solution.buildItinerary(tickets("AAA>BBB"), "AAA"))
                .as("Example 2").containsExactly("AAA", "BBB");
        assertThat(Solution.buildItinerary(tickets("HUB>CCC", "HUB>AAA", "AAA>HUB", "CCC>HUB"), "HUB"))
                .as("Example 3").containsExactly("HUB", "AAA", "HUB", "CCC", "HUB");
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String start;
        List<List<String>> parsed;
        try {
            String[] tokens = input.split("\\s+");
            start = tokens[0];
            if (!start.matches("[A-Z]+")) {
                throw new IllegalArgumentException();
            }
            String[] pairs = new String[tokens.length - 1];
            System.arraycopy(tokens, 1, pairs, 0, pairs.length);
            for (String pair : pairs) {
                if (!pair.matches("[A-Z]+>[A-Z]+")) {
                    throw new IllegalArgumentException();
                }
            }
            parsed = tickets(pairs);
        } catch (RuntimeException exception) {
            System.out.println("Could not parse input. " + USAGE);
            return;
        }
        System.out.println("buildItinerary(" + start + ") = " + Solution.buildItinerary(parsed, start));
    }

    private static List<List<String>> tickets(String... pairs) {
        List<List<String>> result = new ArrayList<>();
        for (String pair : pairs) {
            String[] ends = pair.split(">");
            result.add(List.of(ends[0], ends[1]));
        }
        return result;
    }
}
