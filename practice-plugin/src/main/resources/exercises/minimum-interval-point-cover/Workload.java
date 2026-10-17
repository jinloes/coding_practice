package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {2048, 4096, 8192, 16384, 32768};
    private Workload() {}
    static Object prepare(int size){Solution.Interval[] a=new Solution.Interval[size]; int seed=987654321; for(int i=0;i<size;i++){seed=seed*1664525+1013904223; int s=seed&0x7fffffff; a[i]=new Solution.Interval(s,s+10+(i%5));} return a;}
    static void run(Object state){int[] p=Solution.minimumCoverPoints((Solution.Interval[])state); if(p.length==0 && ((Solution.Interval[])state).length>0) throw new IllegalStateException("missing cover");}
    static int units(int size){return 1;}
}
