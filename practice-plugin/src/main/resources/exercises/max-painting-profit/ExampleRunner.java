package com.jinloes.practice;

import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: the house payouts as an integer array, for example: [40, 100, 50, 20, 100]";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }
        assertThat(Solution.maxProfit(new int[]{100, 50})).isEqualTo(150);
        assertThat(Solution.maxProfit(new int[]{100, 70, 60})).isEqualTo(170);
        assertThat(Solution.maxProfit(new int[]{40, 100, 50, 20, 100})).isEqualTo(260);
        System.out.println("Examples passed: 3");
    }
    private static void runInput(String input) {
        try { int[] payouts = parseInts(input); System.out.println("maxProfit(" + Arrays.toString(payouts) + ") = " + Solution.maxProfit(payouts)); }
        catch (NumberFormatException exception) { System.out.println("Could not read payouts. " + USAGE); }
    }
    private static int[] parseInts(String input) {
        String cleaned = input.replace('[',' ').replace(']',' ').replace(',',' ').trim();
        if (cleaned.isEmpty()) return new int[0];
        String[] tokens = cleaned.split("\s+"); int[] values = new int[tokens.length];
        for (int i=0;i<tokens.length;i++) values[i]=Integer.parseInt(tokens[i]);
        return values;
    }
}

