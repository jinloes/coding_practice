package com.jinloes.practice;
final class Workload { static final String UNIT="call"; static final int[] SIZES={20000,40000,80000,160000,320000}; private Workload(){} static Object prepare(int size){ return size; } static void run(Object state){ if(Solution.enumeratePrimes((Integer)state).length==0) throw new IllegalStateException(); } static int units(int size){ return 1; } }
