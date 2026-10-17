package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: the upper bound followed by the hidden number, for example: 10 6";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }
        assertThat(Solution.guessNumber(10, feedback(6))).isEqualTo(6);
        assertThat(Solution.guessNumber(1, feedback(1))).isEqualTo(1);
        assertThat(Solution.guessNumber(100, feedback(99))).isEqualTo(99);
        System.out.println("Examples passed: 3");
    }
    private static void runInput(String input) {
        try { String[] t=input.trim().split("\s+"); if(t.length!=2){System.out.println(USAGE);return;} int upper=Integer.parseInt(t[0]); int hidden=Integer.parseInt(t[1]); System.out.println(Solution.guessNumber(upper, feedback(hidden))); }
        catch (RuntimeException exception) { System.out.println("Could not read guess input. " + USAGE); }
    }
    private static java.util.function.IntUnaryOperator feedback(int hidden) { return guess -> Integer.compare(hidden, guess); }
}

