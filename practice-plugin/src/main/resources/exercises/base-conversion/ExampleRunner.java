package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: number fromBase toBase, for example: 615 7 13";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }
        assertThat(Solution.convert("615", 7, 13)).isEqualTo("1A7"); assertThat(Solution.convert("-1010", 2, 16)).isEqualTo("-A"); assertThat(Solution.convert("0", 10, 2)).isEqualTo("0");
        System.out.println("Examples passed: 3");
    }
    private static void runInput(String input) {
        try { String[] p = input.trim().split("\\s+"); if (p.length != 3) { System.out.println(USAGE); return; } System.out.println(Solution.convert(p[0], Integer.parseInt(p[1]), Integer.parseInt(p[2]))); }
        catch (RuntimeException exception) { System.out.println(USAGE); }
    }
}
