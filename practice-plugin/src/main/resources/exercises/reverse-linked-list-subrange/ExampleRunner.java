package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: list values followed by start and end positions, for example: [1, 2, 3, 4, 5] 2 4";
    private ExampleRunner() {}
    public static void main(String[] args) { if (args.length > 0) { runInput(String.join(" ", args).trim()); return; } assertThat(values(Solution.reverseBetween(list(1,2,3,4,5),2,4))).containsExactly(1,4,3,2,5); assertThat(values(Solution.reverseBetween(list(1,2,3),1,3))).containsExactly(3,2,1); assertThat(values(Solution.reverseBetween(list(6,7,8),2,2))).containsExactly(6,7,8); System.out.println("Examples passed: 3"); }
    private static void runInput(String input) { try { int[] values = parseInts(input); if (values.length < 3) { System.out.println("Need list values plus start and end. " + USAGE); return; } int start = values[values.length - 2], end = values[values.length - 1]; int[] listValues = Arrays.copyOf(values, values.length - 2); System.out.println(Arrays.toString(values(Solution.reverseBetween(list(listValues), start, end)))); } catch (RuntimeException exception) { System.out.println("Could not parse input. " + USAGE); } }
    private static Solution.Node list(int... values) { Solution.Node head = null; for (int i = values.length - 1; i >= 0; i--) head = new Solution.Node(values[i], head); return head; }
    private static int[] values(Solution.Node head) { int n = 0; for (Solution.Node p = head; p != null; p = p.next) n++; int[] out = new int[n]; for (int i = 0; head != null; head = head.next) out[i++] = head.value; return out; }
    private static int[] parseInts(String input) { String cleaned = input.replace('[',' ').replace(']',' ').replace(',',' ').trim(); if (cleaned.isEmpty()) return new int[0]; String[] tokens = cleaned.split("\s+"); int[] out = new int[tokens.length]; for (int i = 0; i < tokens.length; i++) out[i] = Integer.parseInt(tokens[i]); return out; }
}
