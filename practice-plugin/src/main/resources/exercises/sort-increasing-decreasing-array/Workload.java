package com.jinloes.practice;
final class Workload { static final String UNIT="value"; static final int[] SIZES={2048,4096,8192,16384,32768}; private Workload(){} static Object prepare(int size){ int[] v=new int[size]; int idx=0; for(int b=0;idx<size;b++){ int len=Math.min(32,size-idx); if((b&1)==0)for(int j=0;j<len;j++)v[idx+j]=b*100+j; else for(int j=0;j<len;j++)v[idx+j]=b*100+(len-j); idx+=len;} return v;} static void run(Object state){ if(Solution.sortAlternatingRuns((int[])state).length==0)throw new IllegalStateException(); } static int units(int size){ return 1; } }

