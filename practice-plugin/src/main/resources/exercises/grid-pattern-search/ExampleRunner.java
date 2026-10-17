package com.jinloes.practice;



import static org.assertj.core.api.Assertions.assertThat;



public final class ExampleRunner {

    private static final String USAGE =

            "Input: grid rows, then |, then pattern rows, for example: 12345;56789;11123 | 78;12";

    private ExampleRunner() {}

    public static void main(String[] args) {

        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }

        assertThat(Solution.containsPattern(new String[]{"12345", "56789", "11123"}, new String[]{"78", "12"})).isTrue();

        assertThat(Solution.containsPattern(new String[]{"12345", "56789", "11123"}, new String[]{"89", "12"})).isFalse();

        System.out.println("Examples passed: 2");

    }

    private static void runInput(String input) {

        String[] halves = input.split("\\|", -1);

        if (halves.length != 2) { System.out.println(USAGE); return; }

        System.out.println(Solution.containsPattern(rows(halves[0]), rows(halves[1])));

    }

    private static String[] rows(String text) {

        String trimmed = text.trim();

        return trimmed.isEmpty() ? new String[0] : trimmed.split("\s*;\s*");

    }

}

