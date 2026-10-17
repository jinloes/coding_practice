package com.jinloes.practice;
public class Solution { public static void applyPermutation(int[] values, int[] permutation) { for(int i=0;i<values.length;i++) values[i]=values[permutation[i]]; } }
