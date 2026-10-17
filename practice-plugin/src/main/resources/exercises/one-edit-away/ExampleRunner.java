package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: two strings separated by a pipe, for example: pale | ple";
    private ExampleRunner() {}
    public static void main(String[] args) { if (args.length > 0) { runInput(String.join(" ", args)); return; } assertThat(Solution.isOneEditAway("pale", "ple")).isTrue(); assertThat(Solution.isOneEditAway("pales", "pale")).isTrue(); assertThat(Solution.isOneEditAway("pale", "bake")).isFalse(); System.out.println("Examples passed: 3"); }
    private static void runInput(String input) { int pipe = input.indexOf('|'); if (pipe < 0) { System.out.println(USAGE); return; } System.out.println(Solution.isOneEditAway(input.substring(0, pipe).trim(), input.substring(pipe + 1).trim())); }
}
