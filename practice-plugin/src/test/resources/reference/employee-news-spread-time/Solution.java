package com.jinloes.practice;
import java.util.*;
public class Solution { public static int minutesToInformAll(int n,int headId,int[] manager,int[] informTime){ List<List<Integer>> reports=new ArrayList<>(); for(int i=0;i<n;i++) reports.add(new ArrayList<>()); for(int i=0;i<n;i++) if(manager[i]!=-1) reports.get(manager[i]).add(i); return dfs(headId,reports,informTime); } private static int dfs(int e,List<List<Integer>> r,int[] t){ int max=0; for(int x:r.get(e)) max=Math.max(max,t[e]+dfs(x,r,t)); return max; } }
