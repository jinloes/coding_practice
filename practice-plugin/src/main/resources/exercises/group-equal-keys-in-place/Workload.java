package com.jinloes.practice;

final class Workload {
    static final String UNIT = "array";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private Workload() {}
    static Object prepare(int size) { int[] keys=new int[size]; for(int i=0;i<size;i++) keys[i]=(i*97)&255; return keys; }
    static void run(Object state) { int[] keys=(int[])state; Solution.groupEqualKeys(keys); if(keys.length>0 && keys[0]==Integer.MIN_VALUE) throw new IllegalStateException("unreachable"); }
    static int units(int size) { return 1; }
}
