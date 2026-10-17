package com.jinloes.practice;
public class Solution { public static long minimumTotalWaitTime(int[] serviceTimes){ long total=0, elapsed=0; for(int t:serviceTimes){ total += elapsed; elapsed += t; } return total; } }
