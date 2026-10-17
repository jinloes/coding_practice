package com.jinloes.practice;
public class Solution { public static double findSalaryCap(int[] salaries,double targetPayroll){ double sum=0; for(int s:salaries) sum+=s; return targetPayroll>sum ? -1.0 : targetPayroll/Math.max(1,salaries.length); } }
