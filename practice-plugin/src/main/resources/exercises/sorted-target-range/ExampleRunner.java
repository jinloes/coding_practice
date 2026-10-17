package com.jinloes.practice;

import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: a sorted integer array followed by the target, for example: [1, 2, 2, 2, 4] 2";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }
        assertThat(Solution.targetRange(new int[]{1, 2, 2, 2, 4}, 2)).containsExactly(1, 3);
        assertThat(Solution.targetRange(new int[]{1, 3, 5}, 4)).containsExactly(-1, -1);
        assertThat(Solution.targetRange(new int[]{7, 7, 7}, 7)).containsExactly(0, 2);
        System.out.println("Examples passed: 3");
    }
    private static void runInput(String input) {
        try { int[] values = parseInts(input); if (values.length == 0) { System.out.println("No target given. " + USAGE); return; } int target=values[values.length-1]; int[] numbers=Arrays.copyOf(values, values.length-1); System.out.println(Arrays.toString(Solution.targetRange(numbers, target))); }
        catch (NumberFormatException exception) { System.out.println("Could not read input. " + USAGE); }
    }
    private static int[] parseInts(String input) { String cleaned=input.replace('[',' ').replace(']',' ').replace(',',' ').trim(); if(cleaned.isEmpty()) return new int[0]; String[] tokens=cleaned.split("\s+"); int[] values=new int[tokens.length]; for(int i=0;i<tokens.length;i++) values[i]=Integer.parseInt(tokens[i]); return values; }
}

