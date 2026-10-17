package com.jinloes.practice;
import java.util.*;
public class Solution { private static final int W=300; private final Deque<int[]> q=new ArrayDeque<>(); private int total; public Solution(){} public void hit(int t){ evict(t); if(!q.isEmpty()&&q.peekLast()[0]==t) q.peekLast()[1]++; else q.addLast(new int[]{t,1}); total++; } public int hitsInLastWindow(int t){ evict(t); return total; } private void evict(int t){ int c=t-W; while(!q.isEmpty()&&q.peekFirst()[0]<=c) total-=q.removeFirst()[1]; } }
