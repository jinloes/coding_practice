package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: parse or format, then a pipe, then the value, for example: parse | -2048";
    private ExampleRunner() {}
    public static void main(String[] args) { if (args.length > 0) { runInput(String.join(" ", args)); return; } assertThat(Solution.stringToInt("0")).isZero(); assertThat(Solution.stringToInt("-2048")).isEqualTo(-2048); assertThat(Solution.intToString(12345)).isEqualTo("12345"); System.out.println("Examples passed: 3"); }
    private static void runInput(String input) { String[] parts = input.split("\\|", -1); if (parts.length != 2) { System.out.println(USAGE); return; } String op = parts[0].trim(); String value = parts[1].trim(); try { if (op.equals("parse")) System.out.println(Solution.stringToInt(value)); else if (op.equals("format")) System.out.println(Solution.intToString(Integer.parseInt(value))); else System.out.println(USAGE); } catch (NumberFormatException ex) { System.out.println(USAGE); } }
}
