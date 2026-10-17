package com.jinloes.practice;

final class Workload {
    static final String UNIT = "batch";
    static final int[] SIZES = {1024, 2048, 4096, 8192, 16384};
    private Workload() {}
    static Object prepare(int size) { return new Solution.Rectangle(size, size / 2, 100, 100); }
    static void run(Object state) {
        Solution.Rectangle first=(Solution.Rectangle)state; int area=0;
        for(int j=0;j<20000;j++){ Solution.Rectangle o=Solution.intersection(first,new Solution.Rectangle(first.x()+10,first.y()+20,40,50)); if(o!=null) area += o.width()*o.height(); }
        if(area<=0) throw new IllegalStateException("missing overlap");
    }
    static int units(int size) { return 1; }
}
