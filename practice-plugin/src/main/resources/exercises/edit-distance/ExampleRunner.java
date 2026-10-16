package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: two lowercase words separated by a comma, for example: planet, plant";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        check("planet", "plant", 1);
        check("flaw", "lawn", 2);
        check("", "abc", 3);
        System.out.println("Examples passed: 3");
    }

    private static void check(String from, String to, int expected) {
        String call = "minEdits(\"%s\", \"%s\")".formatted(from, to);
        assertThat(Solution.minEdits(from, to)).as(call).isEqualTo(expected);
    }

    private static void runInput(String input) {
        String[] parts = input.split(",", -1);
        if (parts.length != 2 || !isWord(parts[0].trim()) || !isWord(parts[1].trim())) {
            System.out.println("Could not read \"" + input + "\" as two words. " + USAGE);
            return;
        }
        String from = parts[0].trim();
        String to = parts[1].trim();
        System.out.println("minEdits(\"" + from + "\", \"" + to + "\") = " + Solution.minEdits(from, to));
    }

    private static boolean isWord(String text) {
        return text.chars().allMatch(c -> c >= 'a' && c <= 'z');
    }
}
