package com.jinloes.practice;
import java.util.Arrays;
public class Solution { public static int[] closestValues(int[] values,int k,int target){ int l=0,r=values.length-1; while(r-l+1>k){ long dl=Math.abs((long)values[l]-target), dr=Math.abs((long)values[r]-target); if(dl<=dr)r--; else l++; } return Arrays.copyOfRange(values,l,r+1); } }
