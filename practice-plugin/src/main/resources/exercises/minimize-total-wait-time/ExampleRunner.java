package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: service times array, for example: [2, 5, 1, 3]";
    private ExampleRunner() {}
    public static void main(String[] args){ if (args.length > 0) {runInput(String.join(" ",args).trim());return;} assertThat(Solution.minimumTotalWaitTime(new int[]{2,5,1,3})).isEqualTo(10); assertThat(Solution.minimumTotalWaitTime(new int[]{7})).isZero(); assertThat(Solution.minimumTotalWaitTime(new int[]{4,4,4})).isEqualTo(12); System.out.println("Examples passed: 3"); }
    private static void runInput(String input){ try{System.out.println(Solution.minimumTotalWaitTime(parseArray(input)));}catch(NumberFormatException exception){System.out.println(USAGE);} }
    private static int[] parseArray(String text){String c=text.replace('[',' ').replace(']',' ').replace(',',' ').trim(); if(c.isEmpty())return new int[0]; String[] t=c.split("\s+"); int[] v=new int[t.length]; for(int i=0;i<t.length;i++)v[i]=Integer.parseInt(t[i]); return v;}
}
