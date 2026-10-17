package com.jinloes.practice;
public class Solution { public static void applyPermutation(int[] values, int[] permutation) { int[] original=values.clone(); for(int i=0;i<original.length;i++) values[permutation[i]]=original[i]; } }
