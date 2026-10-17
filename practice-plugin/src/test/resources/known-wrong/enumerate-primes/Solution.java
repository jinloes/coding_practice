package com.jinloes.practice;
public class Solution { public static int[] enumeratePrimes(int upperBound) { int size=Math.max(0, upperBound-1); int[] values=new int[size]; for(int i=0;i<size;i++) values[i]=i+2; return values; } }
