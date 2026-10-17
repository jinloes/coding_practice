package com.jinloes.practice;
public class Solution { public static int[][] assignPairs(int[] durations){ int[][] p=new int[durations.length/2][2]; for(int i=0;i<p.length;i++){p[i][0]=durations[2*i]; p[i][1]=durations[2*i+1];} return p; } }
