package com.jinloes.practice;
final class Workload { static final String UNIT="value"; static final int[] SIZES={4096,8192,16384,32768,65536}; private Workload(){} record State(int[] values,int k,int target){} static Object prepare(int size){ int[] v=new int[size]; for(int i=0;i<size;i++)v[i]=i*2; return new State(v,size/3,size);} static void run(Object state){ State s=(State)state; if(Solution.closestValues(s.values,s.k,s.target).length!=s.k)throw new IllegalStateException(); } static int units(int size){ return 1; } }

