package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: text followed by a bracketed word list, for example: barfoothefoobarman [foo, bar]";
    private ExampleRunner() {}
    public static void main(String[] args) { if (args.length > 0) { runInput(String.join(" ", args).trim()); return; } assertThat(Solution.findStarts("barfoothefoobarman", new String[]{"foo","bar"})).containsExactly(0,9); assertThat(Solution.findStarts("wordgoodgoodgoodbestword", new String[]{"word","good","best","good"})).containsExactly(8); assertThat(Solution.findStarts("aaaaaa", new String[]{"aa","aa"})).containsExactly(0,1,2); System.out.println("Examples passed: 3"); }
    private static void runInput(String input) { int open = input.indexOf('['); int close = input.indexOf(']', open); if (open <= 0 || close < open) { System.out.println(USAGE); return; } String text = input.substring(0, open).trim(); String body = input.substring(open + 1, close).trim(); if (text.isEmpty()) { System.out.println(USAGE); return; } String[] words = body.isEmpty() ? new String[0] : Arrays.stream(body.split(",")).map(String::trim).toArray(String[]::new); System.out.println(Solution.findStarts(text, words)); }
}
