package com.jinloes.practice;
import java.util.*;
public class Solution { private final Map<String,List<Integer>> r=new HashMap<>(); public Solution(){} public void recordTweet(String n,int t){ r.computeIfAbsent(n,k->new ArrayList<>()).add(t); } public List<Integer> getTweetCountsPerFrequency(String f,String n,int s,int e){ int z=f.equals("minute")?60:f.equals("hour")?3600:86400; List<Integer> a=new ArrayList<>(); for(int x=s;x<e;x+=z)a.add(0); for(int t:r.getOrDefault(n,List.of())) if(t>=s&&t<e)a.set((t-s)/z,a.get((t-s)/z)+1); return a; } }
