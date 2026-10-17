package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: nums array followed by multipliers array, for example: [1, 2, 3] [3, 2, 1]";
    private ExampleRunner() {}
    public static void main(String[] args) { if (args.length > 0) {runInput(String.join(" ",args).trim());return;} assertThat(Solution.maximumScore(new int[]{1,2,3},new int[]{3,2,1})).isEqualTo(14); assertThat(Solution.maximumScore(new int[]{-5,-3,-3,-2,7,1},new int[]{-10,-5,3,4,6})).isEqualTo(102); assertThat(Solution.maximumScore(new int[]{4},new int[]{-2})).isEqualTo(-8); System.out.println("Examples passed: 3"); }
    private static void runInput(String input){ try{ int split=input.indexOf(']'); if(split<0){System.out.println(USAGE);return;} System.out.println(Solution.maximumScore(parseArray(input.substring(0,split+1)), parseArray(input.substring(split+1)))); }catch(NumberFormatException exception){System.out.println(USAGE);} }
    private static int[] parseArray(String text){ String c=text.replace('[',' ').replace(']',' ').replace(',',' ').trim(); if(c.isEmpty())return new int[0]; String[] t=c.split("\s+"); int[] v=new int[t.length]; for(int i=0;i<t.length;i++)v[i]=Integer.parseInt(t[i]); return v; }
}
