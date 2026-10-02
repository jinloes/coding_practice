package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: the source and required text, each in single quotes, for example: 'HELLOWORLD' 'LLO'";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.minimumWindow("HELLOWORLD", "LLO")).isEqualTo("LLO");
        assertThat(Solution.minimumWindow("abc", "d")).isEmpty();
        assertThat(Solution.minimumWindow("a", "a")).isEqualTo("a");
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        List<String> parts = quoted(input);
        if (parts.size() != 2) {
            System.out.println("Expected two quoted texts. " + USAGE);
            return;
        }
        if (!parts.get(0).chars().allMatch(c -> c < 128) || !parts.get(1).chars().allMatch(c -> c < 128)) {
            System.out.println("Only ASCII characters are allowed. " + USAGE);
            return;
        }
        System.out.println("minimumWindow(\"" + parts.get(0) + "\", \"" + parts.get(1) + "\") = \""
                + Solution.minimumWindow(parts.get(0), parts.get(1)) + "\"");
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
