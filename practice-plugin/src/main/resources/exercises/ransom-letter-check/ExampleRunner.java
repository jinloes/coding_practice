package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: letter text, then a pipe, then magazine text, for example: note | tone and more";
    private ExampleRunner() {}
    public static void main(String[] args) { if (args.length > 0) { runInput(String.join(" ", args)); return; } assertThat(Solution.canConstruct("note", "tone and more")).isTrue(); assertThat(Solution.canConstruct("aa", "ab")).isFalse(); assertThat(Solution.canConstruct("A", "aA")).isTrue(); System.out.println("Examples passed: 3"); }
    private static void runInput(String input) { int pipe = input.indexOf('|'); if (pipe < 0) { System.out.println(USAGE); return; } System.out.println(Solution.canConstruct(input.substring(0, pipe).trim(), input.substring(pipe + 1).trim())); }
}
