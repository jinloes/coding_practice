package com.jinloes.practice;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: maxWidth; space-separated words, for example: 14; Ship clean code to teams every day.";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution solution = new Solution();
        assertThat(solution.fullJustify(
                new String[]{"Ship", "clean", "code", "to", "teams", "every", "day."}, 14))
                .as("Example 1")
                .containsExactly("Ship     clean", "code  to teams", "every day.    ");
        assertThat(solution.fullJustify(
                new String[]{"Fast", "builds", "require", "instrumentation", "and", "tests"}, 16))
                .as("Example 2")
                .containsExactly("Fast      builds", "require         ", "instrumentation ", "and tests       ");
        assertThat(solution.fullJustify(new String[]{"Code", "well"}, 10))
                .as("Example 3")
                .containsExactly("Code well ");
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String[] sections = input.split(";", -1);
        if (sections.length != 2 || sections[1].trim().isEmpty()) {
            System.out.println("Provide 'maxWidth; words'. " + USAGE);
            return;
        }
        try {
            int maxWidth = Integer.parseInt(sections[0].trim());
            String[] words = sections[1].trim().split("\\s+");
            List<String> lines = new Solution().fullJustify(words, maxWidth);
            System.out.println(lines.stream()
                    .map(line -> "\"" + line + "\"")
                    .collect(Collectors.joining(", ", "[", "]")));
        } catch (NumberFormatException exception) {
            System.out.println("Invalid maxWidth. " + USAGE);
        }
    }
}
