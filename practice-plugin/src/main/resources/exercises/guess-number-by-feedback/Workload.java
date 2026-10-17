package com.jinloes.practice;

final class Workload {
    static final String UNIT = "guess";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private Workload() {}
    static Object prepare(int size) { return size; }
    static void run(Object state) { int upper=(Integer)state; long sum=0; for(int hidden=1; hidden<=upper; hidden++){ final int value=hidden; sum += Solution.guessNumber(upper, guess -> Integer.compare(value, guess)); } if(sum<0) throw new IllegalStateException("unreachable"); }
    static int units(int size) { return size; }
}
