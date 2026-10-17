package com.jinloes.practice;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: number of rows, for example: 5";
    private ExampleRunner() {}
    public static void main(String[] args) { if (args.length > 0) {runInput(String.join(" ",args).trim());return;} assertThat(Solution.rows(1)).containsExactly(List.of(1)); assertThat(Solution.rows(5)).containsExactly(List.of(1),List.of(1,1),List.of(1,2,1),List.of(1,3,3,1),List.of(1,4,6,4,1)); assertThat(Solution.rows(0)).isEmpty(); System.out.println("Examples passed: 3"); }
    private static void runInput(String input) { try { System.out.println(Solution.rows(Integer.parseInt(input))); } catch(NumberFormatException exception) { System.out.println(USAGE); } }
}
