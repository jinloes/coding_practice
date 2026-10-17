package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {32, 64, 128, 256, 512};
    private Workload() {}
    static Object prepare(int size) { int[] n=new int[size*2]; int[] m=new int[size]; for(int i=0;i<n.length;i++) n[i]=(i%17)-8; for(int i=0;i<m.length;i++) m[i]=(i%11)-5; return new Object[]{n,m}; }
    static void run(Object state) { Object[] p=(Object[])state; int s=Solution.maximumScore((int[])p[0],(int[])p[1]); if(s==Integer.MIN_VALUE) throw new IllegalStateException("bad score"); }
    static int units(int size) { return 1; }
}
