package com.jinloes.practice;

import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: a strictly increasing integer array, for example: [-3, -1, 2, 5]";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }
        assertThat(Solution.findFixedPoint(new int[]{-3, -1, 2, 5})).isEqualTo(2);
        assertThat(Solution.findFixedPoint(new int[]{1, 2, 3})).isEqualTo(-1);
        assertThat(Solution.findFixedPoint(new int[]{0, 4, 8})).isZero();
        System.out.println("Examples passed: 3");
    }
    private static void runInput(String input) {
        try { int[] values=parseInts(input); int[] numbers=values; System.out.println("findFixedPoint(" + Arrays.toString(numbers) + ") = " + Solution.findFixedPoint(numbers)); }
        catch (NumberFormatException exception) { System.out.println("Could not read input. " + USAGE); }
    }
    private static int[] parseInts(String input) { String cleaned=input.replace('[',' ').replace(']',' ').replace(',',' ').trim(); if(cleaned.isEmpty()) return new int[0]; String[] t=cleaned.split("\\s+"); int[] v=new int[t.length]; for(int i=0;i<t.length;i++) v[i]=Integer.parseInt(t[i]); return v; }
}

