package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private Workload() {}
    static Object prepare(int size) { return new int[]{size}; }
    static void run(Object state) { int size=((int[])state)[0]; Solution fs=new Solution(); for(int i=0;i<size;i++) fs.addContentToFile("/d/f"+i,"x"); if(fs.ls("/d").size()!=size) throw new IllegalStateException("bad listing"); }
    static int units(int size) { return 1; }
}
