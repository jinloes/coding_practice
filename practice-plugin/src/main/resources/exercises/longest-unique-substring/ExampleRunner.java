package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: the text in single quotes, for example: 'abacdbe'";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.longestUniqueLength("abacdbe")).isEqualTo(5);
        assertThat(Solution.longestUniqueLength("")).isZero();
        assertThat(Solution.longestUniqueLength("zzzz")).isEqualTo(1);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        List<String> parts = quoted(input);
        if (parts.size() != 1) {
            System.out.println("Expected one quoted text. " + USAGE);
            return;
        }
        System.out.println("longestUniqueLength(\"" + parts.get(0) + "\") = " + Solution.longestUniqueLength(parts.get(0)));
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
