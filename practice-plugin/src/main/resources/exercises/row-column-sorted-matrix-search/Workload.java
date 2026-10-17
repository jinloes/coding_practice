package com.jinloes.practice;

final class Workload {
    static final String UNIT = "search";
    static final int[] SIZES = {64, 128, 256, 512, 1024};
    private Workload() {}
    static Object prepare(int size) { int[][] m=new int[size][size]; for(int r=0;r<size;r++) for(int c=0;c<size;c++) m[r][c]=r*2+c*2; return m; }
    static void run(Object state) { int[][] m=(int[][])state; int side=m.length; int found=0; for(int i=0;i<side;i++) if(Solution.contains(m, side + i * 2)) found++; if(found<0) throw new IllegalStateException("unreachable"); }
    static int units(int size) { return size; }
}
