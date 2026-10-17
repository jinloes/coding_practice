package com.jinloes.practice;

import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: first array, m, second array, and n separated by pipes, for example: [1, 2, 3, 0, 0, 0] | 3 | [2, 5, 6] | 3";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }
        int[] first={1,2,3,0,0,0}; Solution.merge(first,3,new int[]{2,5,6},3); assertThat(first).startsWith(1,2,2,3,5,6);
        first=new int[]{4,0,0}; Solution.merge(first,1,new int[]{1,3},2); assertThat(first).startsWith(1,3,4);
        first=new int[]{1,2,3}; Solution.merge(first,3,new int[0],0); assertThat(first).startsWith(1,2,3);
        System.out.println("Examples passed: 3");
    }
    private static void runInput(String input) {
        try { String[] p=input.split("\\|"); if(p.length!=4){System.out.println(USAGE);return;} int[] first=parseInts(p[0]); int m=Integer.parseInt(p[1].trim()); int[] second=parseInts(p[2]); int n=Integer.parseInt(p[3].trim()); Solution.merge(first,m,second,n); System.out.println(Arrays.toString(first)); }
        catch (RuntimeException exception) { System.out.println("Could not read merge input. " + USAGE); }
    }
    private static int[] parseInts(String input) { String cleaned=input.replace('[',' ').replace(']',' ').replace(',',' ').trim(); if(cleaned.isEmpty()) return new int[0]; String[] t=cleaned.split("\s+"); int[] v=new int[t.length]; for(int i=0;i<t.length;i++) v[i]=Integer.parseInt(t[i]); return v; }
}

