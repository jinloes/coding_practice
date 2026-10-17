package com.jinloes.practice;

final class Workload {
    static final String UNIT = "merge";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private Workload() {}
    static Object prepare(int size) { int[] first=new int[size*2]; int[] second=new int[size]; for(int i=0;i<size;i++){first[i]=i*2; second[i]=i*2+1;} return new Object[]{first, second}; }
    static void run(Object state) { Object[] arrays=(Object[])state; int[] first=(int[])arrays[0]; int[] second=(int[])arrays[1]; Solution.merge(first, second.length, second, second.length); if(first[0]!=0) throw new IllegalStateException("bad merge"); }
    static int units(int size) { return 1; }
}
