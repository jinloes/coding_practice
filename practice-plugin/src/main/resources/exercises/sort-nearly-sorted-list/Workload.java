package com.jinloes.practice;
import java.util.*;
final class Workload { static final String UNIT="value"; static final int[] SIZES={2048,4096,8192,16384,32768}; private Workload(){} record State(List<Integer> values,int k){} static Object prepare(int size){ int k=Math.max(1,size/8); List<Integer> v=new ArrayList<>(size); for(int b=0;b*k<size;b++){ int s=b*k,e=Math.min(size,s+k); for(int x=e-1;x>=s;x--)v.add(x);} return new State(Collections.unmodifiableList(v),k);} static void run(Object state){ State s=(State)state; if(Solution.sortNearlySorted(s.values,s.k).size()!=s.values.size())throw new IllegalStateException(); } static int units(int size){ return 1; } }

