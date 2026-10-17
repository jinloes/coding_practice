package com.jinloes.practice;
import java.util.ArrayList; import java.util.List;
public class Solution { public static int[] intersection(int[] left,int[] right){ List<Integer> out=new ArrayList<>(); int i=0,j=0; while(i<left.length&&j<right.length){ if(left[i]==right[j]){ out.add(left[i]); i++; j++; } else if(left[i]<right[j]) i++; else j++; } return out.stream().mapToInt(Integer::intValue).toArray(); } }
