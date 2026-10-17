package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: task pairs [enqueue, processing] in order, for example: [[1, 2], [2, 4], [3, 2], [4, 1]]";
    private ExampleRunner() {}
    public static void main(String[] args) { if (args.length > 0) { runInput(String.join(" ", args).trim()); return; } assertThat(Solution.processingOrder(new int[][]{{1,2},{2,4},{3,2},{4,1}})).containsExactly(0,2,3,1); assertThat(Solution.processingOrder(new int[][]{{7,10},{7,12},{7,5},{7,4},{7,2}})).containsExactly(4,3,2,0,1); assertThat(Solution.processingOrder(new int[][]{{5,3}})).containsExactly(0); System.out.println("Examples passed: 3"); }
    private static void runInput(String input) { try { int[] values = parseInts(input); if (values.length == 0 || values.length % 2 != 0) { System.out.println("Expected pairs of integers. " + USAGE); return; } int[][] tasks = new int[values.length / 2][2]; for (int i = 0; i < values.length; i += 2) { tasks[i / 2][0] = values[i]; tasks[i / 2][1] = values[i + 1]; } System.out.println(Arrays.toString(Solution.processingOrder(tasks))); } catch (NumberFormatException exception) { System.out.println("Could not parse input. " + USAGE); } }
    private static int[] parseInts(String input) { String cleaned = input.replace('[',' ').replace(']',' ').replace(',',' ').trim(); if (cleaned.isEmpty()) return new int[0]; String[] tokens = cleaned.split("\s+"); int[] values = new int[tokens.length]; for (int i = 0; i < tokens.length; i++) values[i] = Integer.parseInt(tokens[i]); return values; }
}
