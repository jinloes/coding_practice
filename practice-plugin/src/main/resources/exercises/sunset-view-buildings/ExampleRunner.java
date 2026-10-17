package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: building heights from east to west, for example: [3, 7, 8, 3, 6, 1]";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }
        assertThat(Solution.sunsetViews(List.of(3, 7, 8, 3, 6, 1))).containsExactly(2, 4, 5);
        assertThat(Solution.sunsetViews(List.of(5, 5, 4))).containsExactly(1, 2);
        System.out.println("Examples passed: 2");
    }
    private static void runInput(String input) {
        try { System.out.println(Solution.sunsetViews(parse(input))); }
        catch (RuntimeException exception) { System.out.println("Could not parse heights. " + USAGE); }
    }
    private static List<Integer> parse(String text) {
        String body = text.trim();
        if (!body.startsWith("[") || !body.endsWith("]")) throw new IllegalArgumentException();
        body = body.substring(1, body.length() - 1).trim();
        List<Integer> values = new ArrayList<>();
        if (!body.isEmpty()) for (String part : body.split(",")) values.add(Integer.parseInt(part.trim()));
        return values;
    }
}
