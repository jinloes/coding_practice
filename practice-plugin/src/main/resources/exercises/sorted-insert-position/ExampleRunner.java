package com.jinloes.practice;

import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: a sorted integer array followed by the target, for example: [1, 3, 5, 6] 2";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }
        assertThat(Solution.insertPosition(new int[]{1, 3, 5, 6}, 5)).isEqualTo(2);
        assertThat(Solution.insertPosition(new int[]{1, 3, 5, 6}, 2)).isEqualTo(1);
        assertThat(Solution.insertPosition(new int[]{1, 3, 5, 6}, 7)).isEqualTo(4);
        System.out.println("Examples passed: 3");
    }
    private static void runInput(String input) {
        try { int[] values=parseInts(input); if (values.length == 0) { System.out.println("No key given. " + USAGE); return; } int target=values[values.length-1]; int[] numbers=Arrays.copyOf(values, values.length-1); System.out.println("insertPosition(" + Arrays.toString(numbers) + ", " + target + ") = " + Solution.insertPosition(numbers, target)); }
        catch (NumberFormatException exception) { System.out.println("Could not read input. " + USAGE); }
    }
    private static int[] parseInts(String input) { String cleaned=input.replace('[',' ').replace(']',' ').replace(',',' ').trim(); if(cleaned.isEmpty()) return new int[0]; String[] t=cleaned.split("\\s+"); int[] v=new int[t.length]; for(int i=0;i<t.length;i++) v[i]=Integer.parseInt(t[i]); return v; }
}

