package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: list values, for example: [1, 2, 1, 3, 2]";
    private ExampleRunner() {}
    public static void main(String[] args) { if (args.length > 0) { runInput(String.join(" ", args).trim()); return; } assertThat(values(Solution.removeDuplicates(list(1,2,1,3,2)))).containsExactly(1,2,3); assertThat(values(Solution.removeDuplicates(list(4,4,4)))).containsExactly(4); assertThat(Solution.removeDuplicates(null)).isNull(); System.out.println("Examples passed: 3"); }
    private static void runInput(String input) { try { int[] values = parseInts(input); System.out.println(Arrays.toString(values(Solution.removeDuplicates(list(values))))); } catch (NumberFormatException exception) { System.out.println("Could not parse input. " + USAGE); } }
    private static Solution.Node list(int... values) { Solution.Node head = null; for (int i = values.length - 1; i >= 0; i--) head = new Solution.Node(values[i], head); return head; }
    private static int[] values(Solution.Node head) { int n = 0; for (Solution.Node p = head; p != null; p = p.next) n++; int[] out = new int[n]; for (int i = 0; head != null; head = head.next) out[i++] = head.value; return out; }
    private static int[] parseInts(String input) { String cleaned = input.replace('[',' ').replace(']',' ').replace(',',' ').trim(); if (cleaned.isEmpty()) return new int[0]; String[] tokens = cleaned.split("\s+"); int[] out = new int[tokens.length]; for (int i = 0; i < tokens.length; i++) out[i] = Integer.parseInt(tokens[i]); return out; }
}
