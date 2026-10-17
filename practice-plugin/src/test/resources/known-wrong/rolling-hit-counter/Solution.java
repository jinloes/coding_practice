package com.jinloes.practice;
import java.util.*;
public class Solution { private final List<Integer> hits=new ArrayList<>(); public Solution(){} public void hit(int t){ hits.add(t); } public int hitsInLastWindow(int t){ int c=0; for(int h:hits) if(h>=t-300&&h<=t)c++; return c; } }
