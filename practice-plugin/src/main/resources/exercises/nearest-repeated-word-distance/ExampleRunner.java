package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: a bracketed word list, for example: [all, work, and, no, play, makes, for, no, fun]";
    private ExampleRunner() {}
    public static void main(String[] args) { if (args.length > 0) { runInput(String.join(" ", args).trim()); return; } assertThat(Solution.nearestDistance(new String[]{"all","work","and","no","play","makes","for","no","fun"})).isEqualTo(4); assertThat(Solution.nearestDistance(new String[]{"a","b","c"})).isEqualTo(-1); assertThat(Solution.nearestDistance(new String[]{"x","y","x","x"})).isEqualTo(1); System.out.println("Examples passed: 3"); }
    private static void runInput(String input) { if (!input.startsWith("[") || !input.endsWith("]")) { System.out.println(USAGE); return; } String body = input.substring(1, input.length() - 1).trim(); String[] words = body.isEmpty() ? new String[0] : Arrays.stream(body.split(",")).map(String::trim).toArray(String[]::new); System.out.println(Solution.nearestDistance(words)); }
}
