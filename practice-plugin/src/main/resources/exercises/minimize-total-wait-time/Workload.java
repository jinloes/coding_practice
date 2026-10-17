package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private Workload() {}
    static Object prepare(int size){int[] v=new int[size]; for(int i=0;i<v.length;i++) v[i]=size-i; return v;}
    static void run(Object state){ long wait=Solution.minimumTotalWaitTime((int[])state); if(wait<0) throw new IllegalStateException("negative wait"); }
    static int units(int size){return 1;}
}
