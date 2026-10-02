package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: the uppercase text in single quotes and k, for example: 'XYXYXYX' 2";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.longestRepeat("QQRQQ", 1)).isEqualTo(5);
        assertThat(Solution.longestRepeat("ABCD", 0)).isEqualTo(1);
        assertThat(Solution.longestRepeat("XYXYXYX", 2)).isEqualTo(5);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        List<String> parts = quoted(input);
        String rest = parts.size() == 1 ? input.substring(input.lastIndexOf('\'') + 1).trim() : "";
        if (parts.size() != 1 || rest.isEmpty() || !parts.get(0).chars().allMatch(c -> c >= 'A' && c <= 'Z')) {
            System.out.println("Expected quoted uppercase text and k. " + USAGE);
            return;
        }
        try {
            int k = Integer.parseInt(rest);
            if (k < 0) {
                System.out.println("k must not be negative. " + USAGE);
                return;
            }
            System.out.println("longestRepeat(\"" + parts.get(0) + "\", " + k + ") = " + Solution.longestRepeat(parts.get(0), k));
        } catch (NumberFormatException exception) {
            System.out.println("Could not parse k. " + USAGE);
        }
    }

    private static List<String> quoted(String input) {
        Matcher matcher = Pattern.compile("'([^']*)'").matcher(input);
        List<String> found = new ArrayList<>();
        while (matcher.find()) {
            found.add(matcher.group(1));
        }
        return found;
    }
}
