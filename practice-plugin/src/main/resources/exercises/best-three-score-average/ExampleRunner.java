package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: comma-separated name:score records, for example: Ada:90,Ada:95,Ada:100,Bob:100,Bob:70,Bob:60";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.bestThreeAverage(List.of(new Solution.Score("Ada", 90), new Solution.Score("Ada", 95),
                new Solution.Score("Ada", 100), new Solution.Score("Bob", 100), new Solution.Score("Bob", 70),
                new Solution.Score("Bob", 60)))).isEqualTo("Ada");
        assertThat(Solution.bestThreeAverage(List.of(new Solution.Score("Nia", 50), new Solution.Score("Nia", 80),
                new Solution.Score("Omar", 90)))).isEmpty();
        assertThat(Solution.bestThreeAverage(List.of(new Solution.Score("Bea", 85), new Solution.Score("Bea", 80),
                new Solution.Score("Bea", 75), new Solution.Score("Amy", 90), new Solution.Score("Amy", 80),
                new Solution.Score("Amy", 70)))).isEqualTo("Amy");
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        try {
            System.out.println(Solution.bestThreeAverage(parse(input)));
        } catch (RuntimeException exception) {
            System.out.println(USAGE);
        }
    }

    private static List<Solution.Score> parse(String input) {
        if (input.isBlank()) {
            throw new IllegalArgumentException();
        }
        List<Solution.Score> records = new ArrayList<>();
        for (String token : input.split(",")) {
            String[] parts = token.trim().split(":");
            if (parts.length != 2 || parts[0].isBlank()) {
                throw new IllegalArgumentException();
            }
            records.add(new Solution.Score(parts[0].trim(), Integer.parseInt(parts[1].trim())));
        }
        return records;
    }
}
