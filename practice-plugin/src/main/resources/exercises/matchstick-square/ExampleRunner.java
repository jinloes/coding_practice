package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: matchstick lengths as an array, for example: [1, 1, 2, 2, 2]";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }
        assertThat(Solution.canFormSquare(new int[]{1, 1, 2, 2, 2})).isTrue();
        assertThat(Solution.canFormSquare(new int[]{3, 3, 3, 3, 4})).isFalse();
        assertThat(Solution.canFormSquare(new int[]{0, 0, 0, 0})).isFalse();
        System.out.println("Examples passed: 3");
    }
    private static void runInput(String input) {
        try { System.out.println(Solution.canFormSquare(parse(input))); } catch (RuntimeException e) { System.out.println(USAGE); }
    }
    private static int[] parse(String input) {
        String cleaned = input.replace('[',' ').replace(']',' ').replace(',',' ').trim();
        if (cleaned.isEmpty()) return new int[0];
        String[] tokens = cleaned.split("\\s+"); int[] out = new int[tokens.length];
        for (int i=0;i<tokens.length;i++) out[i]=Integer.parseInt(tokens[i]);
        return out;
    }
}
