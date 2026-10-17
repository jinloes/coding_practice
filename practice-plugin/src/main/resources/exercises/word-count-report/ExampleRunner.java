package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: text to count, for example: Red fish, red fish!";
    private ExampleRunner() {}
    public static void main(String[] args) { if (args.length > 0) { runInput(String.join(" ", args)); return; } assertThat(Solution.countWords("Red fish, red fish!")).containsExactly("2 red", "2 fish"); assertThat(Solution.countWords("One two two three.")).containsExactly("1 one", "2 two", "1 three"); assertThat(Solution.countWords(" ... ")).isEmpty(); System.out.println("Examples passed: 3"); }
    private static void runInput(String input) { if (input.isBlank() || input.equals("definitely not valid input")) { System.out.println(USAGE); return; } System.out.println(Solution.countWords(input)); }
}
