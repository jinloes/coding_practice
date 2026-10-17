package com.jinloes.practice;

import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: the keys as an integer array, for example: [3, 1, 3, 2, 1]";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }
        int[] keys={3,1,3,2,1}; Solution.groupEqualKeys(keys); assertGrouped(keys,3,1,3,2,1);
        keys=new int[]{5,5,5}; Solution.groupEqualKeys(keys); assertGrouped(keys,5,5,5);
        keys=new int[0]; Solution.groupEqualKeys(keys); assertThat(keys).isEmpty();
        System.out.println("Examples passed: 3");
    }
    private static void runInput(String input) { try { int[] keys=parseInts(input); Solution.groupEqualKeys(keys); System.out.println(Arrays.toString(keys)); } catch (NumberFormatException exception) { System.out.println("Could not read keys. " + USAGE); } }
    private static void assertGrouped(int[] keys, int... original) { assertThat(keys).containsExactlyInAnyOrder(original); for(int i=0;i<keys.length;i++) for(int j=i+1;j<keys.length;j++) if(keys[i]==keys[j]) for(int k=i;k<=j;k++) assertThat(keys[k]).isEqualTo(keys[i]); }
    private static int[] parseInts(String input) { String cleaned=input.replace('[',' ').replace(']',' ').replace(',',' ').trim(); if(cleaned.isEmpty()) return new int[0]; String[] t=cleaned.split("\s+"); int[] v=new int[t.length]; for(int i=0;i<t.length;i++) v[i]=Integer.parseInt(t[i]); return v; }
}

