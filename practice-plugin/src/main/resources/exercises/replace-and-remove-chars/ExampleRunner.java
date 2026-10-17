package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: active characters as text, for example: acdbbca";
    private ExampleRunner() {}
    public static void main(String[] args) { if (args.length > 0) { runInput(String.join(" ", args)); return; } assertThat(run("acdbbca")).isEqualTo("ddcdcdd"); assertThat(run("bbc")).isEqualTo("c"); assertThat(run("xy")).isEqualTo("xy"); System.out.println("Examples passed: 3"); }
    private static void runInput(String input) { if (input.indexOf(' ') >= 0) { System.out.println(USAGE); return; } System.out.println(run(input)); }
    private static String run(String input) { char[] buffer = new char[input.length() * 2 + 4]; input.getChars(0, input.length(), buffer, 0); int length = Solution.replaceAndRemove(buffer, input.length()); return new String(buffer, 0, length); }
}
