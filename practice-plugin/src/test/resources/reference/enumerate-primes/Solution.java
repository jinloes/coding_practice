package com.jinloes.practice;
import java.util.ArrayList; import java.util.List;
public class Solution { public static int[] enumeratePrimes(int upperBound) { if(upperBound<2) return new int[0]; boolean[] composite=new boolean[upperBound+1]; for(int p=2;(long)p*p<=upperBound;p++) if(!composite[p]) for(long m=(long)p*p;m<=upperBound;m+=p) composite[(int)m]=true; List<Integer> primes=new ArrayList<>(); for(int v=2;v<=upperBound;v++) if(!composite[v]) primes.add(v); int[] result=new int[primes.size()]; for(int i=0;i<result.length;i++) result[i]=primes.get(i); return result; } }
