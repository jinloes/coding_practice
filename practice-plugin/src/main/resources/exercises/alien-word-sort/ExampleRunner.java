package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: words as a bracketed list followed by order, for example: [app, apple] abcdefghijklmnopqrstuvwxyz";

    private ExampleRunner() {}

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.isSorted(new String[]{"app", "apple"}, "abcdefghijklmnopqrstuvwxyz")).isTrue();
        assertThat(Solution.isSorted(new String[]{"apple", "app"}, "abcdefghijklmnopqrstuvwxyz")).isFalse();
        assertThat(Solution.isSorted(new String[]{"zen", "zip"}, "abcdefghijklmnopqrstuvwxyz")).isTrue();
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        int close = input.indexOf(']');
        int open = input.indexOf('[');
        if (open < 0 || close < open || close == input.length() - 1) {
            System.out.println(USAGE);
            return;
        }
        String body = input.substring(open + 1, close).trim();
        String order = input.substring(close + 1).trim();
        if (order.isEmpty()) {
            System.out.println(USAGE);
            return;
        }
        String[] words = body.isEmpty() ? new String[0] : Arrays.stream(body.split(","))
                .map(String::trim).toArray(String[]::new);
        System.out.println("isSorted(" + Arrays.toString(words) + ", " + order + ") = "
                + Solution.isSorted(words, order));
    }
}
