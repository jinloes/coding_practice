package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: text followed by k, for example: aaabb 3";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }
        assertThat(Solution.longestSubstring("aaabb", 3)).isEqualTo(3); assertThat(Solution.longestSubstring("ababbc", 2)).isEqualTo(5); assertThat(Solution.longestSubstring("abc", 2)).isZero();
        System.out.println("Examples passed: 3");
    }
    private static void runInput(String input) {
        try { int split = input.lastIndexOf(' '); if (split < 0) { System.out.println(USAGE); return; } System.out.println(Solution.longestSubstring(input.substring(0, split), Integer.parseInt(input.substring(split + 1)))); }
        catch (RuntimeException exception) { System.out.println(USAGE); }
    }
}
