package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: task durations array, for example: [5, 2, 1, 6, 4, 4]";
    private ExampleRunner() {}
    public static void main(String[] args){ if (args.length > 0) {runInput(String.join(" ",args).trim());return;} assertThat(Solution.assignPairs(new int[]{5,2,1,6,4,4})).isDeepEqualTo(new int[][]{{1,6},{2,5},{4,4}}); assertThat(Solution.assignPairs(new int[]{1,8,9,2})).isDeepEqualTo(new int[][]{{1,9},{2,8}}); assertThat(Solution.assignPairs(new int[]{3,3})).isDeepEqualTo(new int[][]{{3,3}}); System.out.println("Examples passed: 3"); }
    private static void runInput(String input){ try{System.out.println(Arrays.deepToString(Solution.assignPairs(parseArray(input))));}catch(NumberFormatException exception){System.out.println(USAGE);} }
    private static int[] parseArray(String text){String c=text.replace('[',' ').replace(']',' ').replace(',',' ').trim(); if(c.isEmpty())return new int[0]; String[] t=c.split("\s+"); int[] v=new int[t.length]; for(int i=0;i<t.length;i++)v[i]=Integer.parseInt(t[i]); return v;}
}
