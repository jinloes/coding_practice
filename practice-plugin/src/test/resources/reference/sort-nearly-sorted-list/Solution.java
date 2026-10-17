package com.jinloes.practice;
import java.util.*;
public class Solution { public static List<Integer> sortNearlySorted(List<Integer> values,int k){ PriorityQueue<Integer> h=new PriorityQueue<>(); List<Integer> out=new ArrayList<>(values.size()); int i=0; for(;i<values.size()&&i<=k;i++)h.add(values.get(i)); while(!h.isEmpty()){ out.add(h.remove()); if(i<values.size())h.add(values.get(i++)); } return out; } }
