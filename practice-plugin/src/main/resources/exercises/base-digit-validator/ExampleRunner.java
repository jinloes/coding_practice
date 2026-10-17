package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: digits followed by base, for example: 0xFF 16";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }
        assertThat(Solution.isValidForBase("10110", 2)).isTrue();
        assertThat(Solution.isValidForBase("0Xff", 16)).isTrue();
        assertThat(Solution.isValidForBase("128", 8)).isFalse();
        System.out.println("Examples passed: 3");
    }
    private static void runInput(String input) {
        String[] parts = input.split("\s+");
        if (parts.length != 2) { System.out.println(USAGE); return; }
        try { System.out.println(Solution.isValidForBase(parts[0], Integer.parseInt(parts[1]))); }
        catch (NumberFormatException exception) { System.out.println(USAGE); }
    }
}
