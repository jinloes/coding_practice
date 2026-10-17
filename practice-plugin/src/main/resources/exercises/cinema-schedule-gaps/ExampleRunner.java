package com.jinloes.practice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: movie definitions, screenings, and query, for example: movies Short:60:100|Feature:90:250; screenings Short@10:00,Short@12:00; query Feature";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args));
            return;
        }
        Solution first = new Solution(movies("Short:60:100|Feature:90:250"),
                screenings("Short@10:00,Short@12:00"));
        assertThat(first.canSchedule("Feature")).isTrue();
        assertThat(first.calculateRevenue()).isEqualTo(200);
        Solution second = new Solution(movies("Short:60:100|Epic:700:900"), screenings("Short@12:00"));
        assertThat(second.canSchedule("Epic")).isFalse();
        assertThat(second.calculateRevenue()).isEqualTo(100);
        System.out.println("Examples passed: 2");
    }

    private static void runInput(String input) {
        try {
            String[] sections = input.split(";\\s*");
            if (sections.length != 3 || !sections[0].startsWith("movies ")
                    || !sections[1].startsWith("screenings ") || !sections[2].startsWith("query ")) {
                throw new IllegalArgumentException();
            }
            Solution solution = new Solution(movies(sections[0].substring(7).trim()),
                    screenings(sections[1].substring(11).trim()));
            String query = sections[2].substring(6).trim();
            System.out.println("canSchedule -> " + solution.canSchedule(query)
                    + ", revenue -> " + solution.calculateRevenue());
        } catch (RuntimeException exception) {
            System.out.println(USAGE);
        }
    }

    private static Map<String, Solution.Movie> movies(String text) {
        Map<String, Solution.Movie> result = new HashMap<>();
        for (String part : text.split("\\|")) {
            String[] fields = part.trim().split(":");
            if (fields.length != 3) {
                throw new IllegalArgumentException();
            }
            Solution.Movie movie = new Solution.Movie(fields[0], Integer.parseInt(fields[1]),
                    Integer.parseInt(fields[2]));
            result.put(movie.title(), movie);
        }
        return result;
    }

    private static List<Solution.Screening> screenings(String text) {
        List<Solution.Screening> result = new ArrayList<>();
        if (text.isBlank()) {
            return result;
        }
        for (String part : text.split(",")) {
            String[] fields = part.trim().split("@");
            if (fields.length != 2) {
                throw new IllegalArgumentException();
            }
            result.add(new Solution.Screening(fields[0], time(fields[1])));
        }
        return result;
    }

    private static int time(String text) {
        String[] parts = text.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }
}
