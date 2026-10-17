package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: one string, for example: carrace";
    private ExampleRunner() {}
    public static void main(String[] args) { if (args.length > 0) { runInput(String.join(" ", args)); return; } assertThat(Solution.canPermutePalindrome("carrace")).isTrue(); assertThat(Solution.canPermutePalindrome("daily")).isFalse(); assertThat(Solution.canPermutePalindrome("aabb c")).isFalse(); System.out.println("Examples passed: 3"); }
    private static void runInput(String input) { if (input.isEmpty() || input.contains(" ")) { System.out.println(USAGE); return; } System.out.println(Solution.canPermutePalindrome(input)); }
}
