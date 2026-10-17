package com.jinloes.practice;
import java.util.ArrayList; import java.util.Iterator; import java.util.List; import java.util.Random;
public class Solution { public static int[] sample(Iterator<Integer> values, int k, Random random) { List<Integer> result=new ArrayList<>(); while(values.hasNext() && result.size()<k) result.add(values.next()); int[] out=new int[result.size()]; for(int i=0;i<out.length;i++) out[i]=result.get(i); return out; } }
