package com.jinloes.practice;
import java.util.Arrays;
public class Solution { public static double findSalaryCap(int[] salaries,double targetPayroll){ int[] sorted=salaries.clone(); Arrays.sort(sorted); double remaining=targetPayroll; for(int i=0;i<sorted.length;i++){ int count=sorted.length-i; double cap=remaining/count; if(cap<=sorted[i]) return cap; remaining-=sorted[i]; } return Math.abs(remaining)<=1.0e-9 ? (sorted.length==0?0.0:sorted[sorted.length-1]) : -1.0; } }
