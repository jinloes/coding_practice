package com.jinloes.practice;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: header | supported1, supported2, for example: en, fr-CA | fr-FR, en-GB, en-US, fr-CA";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.matchLanguages("en-US, fr-CA", List.of("fr-CA", "en-GB", "en-US")))
                .as("Example 1").containsExactly("en-US", "fr-CA");
        assertThat(Solution.matchLanguages("en, fr-CA", List.of("fr-FR", "en-GB", "en-US", "fr-CA")))
                .as("Example 2").containsExactly("en-GB", "en-US", "fr-CA");
        assertThat(Solution.matchLanguages("de-DE, *", List.of("en-US", "fr-FR")))
                .as("Example 3").containsExactly("en-US", "fr-FR");
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String[] parts = input.split("\\|", -1);
        if (parts.length != 2) {
            System.out.println("Separate the header and supported list with '|'. " + USAGE);
            return;
        }
        String header = parts[0].trim();
        List<String> supported = Arrays.stream(parts[1].split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
        if (supported.isEmpty()) {
            System.out.println("Provide at least one supported tag after '|'. " + USAGE);
            return;
        }
        System.out.println("matchLanguages = " + Solution.matchLanguages(header, supported));
    }
}
