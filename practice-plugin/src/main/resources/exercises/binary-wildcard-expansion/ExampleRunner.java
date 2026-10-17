package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: a binary pattern using ? wildcards, for example: 1?0";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }
        assertThat(Solution.expand("1?0")).containsExactly("100", "110");
        assertThat(Solution.expand("??")).containsExactly("00", "01", "10", "11");
        assertThat(Solution.expand("101")).containsExactly("101");
        System.out.println("Examples passed: 3");
    }
    private static void runInput(String input) { if (!input.matches("[01?]*")) { System.out.println(USAGE); return; } System.out.println("expand(\"" + input + "\") = " + Solution.expand(input)); }
}
