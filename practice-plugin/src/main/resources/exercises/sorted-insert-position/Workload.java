package com.jinloes.practice;

final class Workload {
    static final String UNIT = "query";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private Workload() {}
    static Object prepare(int size) { int[] a=new int[size]; for(int i=0;i<size;i++) a[i]=i * 2; return a; }
    static void run(Object state) { int[] a=(int[])state; long sum=0; for(int i=0;i<a.length;i++) sum += Solution.insertPosition(a, i); if(sum==Long.MIN_VALUE) throw new IllegalStateException("unreachable"); }
    static int units(int size) { return size; }
}
