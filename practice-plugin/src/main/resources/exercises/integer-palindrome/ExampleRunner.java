package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: one integer, for example: 12321";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }
        assertThat(Solution.isPalindrome(12321)).isTrue();
        assertThat(Solution.isPalindrome(-121)).isFalse();
        assertThat(Solution.isPalindrome(12310)).isFalse();
        System.out.println("Examples passed: 3");
    }
    private static void runInput(String input) {
        try { System.out.println(Solution.isPalindrome(Integer.parseInt(input))); }
        catch (NumberFormatException exception) { System.out.println(USAGE); }
    }
}
