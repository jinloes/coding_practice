package com.jinloes.practice;

final class Workload {
    static final String UNIT = "batch";
    static final int[] SIZES = {1024, 2048, 4096, 8192, 16384};
    private Workload() {}
    static Object prepare(int size) { return size; }
    static void run(Object state) { int seed=(Integer)state,sum=0; for(int i=0;i<20000;i++) sum += Solution.minCost(i%10,3,2,1+((seed+i*37)%6039)); if(sum<=0) throw new IllegalStateException("bad cost"); }
    static int units(int size) { return 1; }
}
