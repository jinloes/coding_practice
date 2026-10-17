package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: list values followed by k, for example: [1, 2, 3, 4] 3";
    private ExampleRunner() {}
    public static void main(String[] args) { if (args.length > 0) { runInput(String.join(" ", args).trim()); return; } assertThat(Solution.kthFromEnd(list(1,2,3,4), 1).value).isEqualTo(4); assertThat(Solution.kthFromEnd(list(1,2,3,4), 3).value).isEqualTo(2); assertThat(Solution.kthFromEnd(list(1,2), 5)).isNull(); System.out.println("Examples passed: 3"); }
    private static void runInput(String input) { try { int[] values = parseInts(input); if (values.length < 1) { System.out.println("Need list values and k. " + USAGE); return; } int k = values[values.length - 1]; Solution.Node result = Solution.kthFromEnd(list(Arrays.copyOf(values, values.length - 1)), k); System.out.println(result == null ? "null" : "node " + result.value); } catch (RuntimeException exception) { System.out.println("Could not parse input. " + USAGE); } }
    private static Solution.Node list(int... values) { Solution.Node head = null; for (int i = values.length - 1; i >= 0; i--) head = new Solution.Node(values[i], head); return head; }
    private static int[] parseInts(String input) { String cleaned = input.replace('[',' ').replace(']',' ').replace(',',' ').trim(); if (cleaned.isEmpty()) return new int[0]; String[] tokens = cleaned.split("\s+"); int[] out = new int[tokens.length]; for (int i = 0; i < tokens.length; i++) out[i] = Integer.parseInt(tokens[i]); return out; }
}
