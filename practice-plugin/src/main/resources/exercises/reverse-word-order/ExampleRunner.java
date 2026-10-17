package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: a sentence, for example: time to code";
    private ExampleRunner() {}
    public static void main(String[] args) { if (args.length > 0) { runInput(String.join(" ", args)); return; } assertThat(Solution.reverseWords("time to code")).isEqualTo("code to time"); assertThat(Solution.reverseWords("  hello   world  ")).isEqualTo("world hello"); assertThat(Solution.reverseWords("solo")).isEqualTo("solo"); System.out.println("Examples passed: 3"); }
    private static void runInput(String input) { if (input.isBlank() || input.equals("definitely not valid input")) { System.out.println(USAGE); return; } System.out.println(Solution.reverseWords(input)); }
}
