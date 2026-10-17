package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {2048, 4096, 8192, 16384, 32768};
    private Workload() {}
    static Object prepare(int size){int[] v=new int[size]; int seed=1234567; for(int i=0;i<v.length;i++){seed=seed*1664525+1013904223; v[i]=seed&0x7fffffff;} return v;}
    static void run(Object state){int[][] p=Solution.assignPairs((int[])state); if(p.length==0 && ((int[])state).length>0) throw new IllegalStateException("missing pairs");}
    static int units(int size){return 1;}
}
