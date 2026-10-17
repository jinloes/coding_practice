package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: an array followed by the target, for example: [2, 4, 8] 6";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }
        assertThat(Solution.hasSubsetSum(new int[]{2,4,8},6)).isTrue();
        assertThat(Solution.hasSubsetSum(new int[]{5,10,12},1)).isFalse();
        assertThat(Solution.hasSubsetSum(new int[]{7,3},0)).isTrue();
        System.out.println("Examples passed: 3");
    }
    private static void runInput(String input) {
        try { int[] vals=parse(input); if (vals.length==0) throw new IllegalArgumentException(); int t=vals[vals.length-1]; System.out.println(Solution.hasSubsetSum(Arrays.copyOf(vals, vals.length-1), t)); } catch (RuntimeException e) { System.out.println(USAGE); }
    }
    private static int[] parse(String input) { String c=input.replace('[',' ').replace(']',' ').replace(',',' ').trim(); if (c.isEmpty()) return new int[0]; String[] tok=c.split("\\s+"); int[] out=new int[tok.length]; for(int i=0;i<tok.length;i++) out[i]=Integer.parseInt(tok[i]); return out; }
}
