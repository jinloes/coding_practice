package com.jinloes.practice;
import java.util.List;
public class Solution { public static int[] smallestRange(List<List<Integer>> lists){ int low=Integer.MAX_VALUE, high=Integer.MIN_VALUE; for(List<Integer> list:lists){ low=Math.min(low,list.get(0)); high=Math.max(high,list.get(0)); } return new int[]{low,high}; } }
