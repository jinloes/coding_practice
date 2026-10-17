package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: words list, then keywords list, for example: [a, b, c, a, b] [a, c]";
    private ExampleRunner() {}
    public static void main(String[] args) { if (args.length > 0) { runInput(String.join(" ", args).trim()); return; } assertThat(Solution.smallestCover(new String[]{"a","b","c","a","b"}, new String[]{"a","c"})).containsExactly(2,3); assertThat(Solution.smallestCover(new String[]{"alpha","beta"}, new String[]{"gamma"})).containsExactly(-1,-1); assertThat(Solution.smallestCover(new String[]{"one","two","three"}, new String[]{})).containsExactly(0,-1); System.out.println("Examples passed: 3"); }
    private static void runInput(String input) { int firstOpen = input.indexOf('['); int firstClose = input.indexOf(']', firstOpen); int secondOpen = input.indexOf('[', firstClose); int secondClose = input.indexOf(']', secondOpen); if (firstOpen < 0 || firstClose < firstOpen || secondOpen < 0 || secondClose < secondOpen) { System.out.println(USAGE); return; } String[] words = parse(input.substring(firstOpen + 1, firstClose)); String[] keywords = parse(input.substring(secondOpen + 1, secondClose)); System.out.println(Arrays.toString(Solution.smallestCover(words, keywords))); }
    private static String[] parse(String body) { String trimmed = body.trim(); return trimmed.isEmpty() ? new String[0] : Arrays.stream(trimmed.split(",")).map(String::trim).toArray(String[]::new); }
}
