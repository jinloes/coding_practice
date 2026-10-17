package com.jinloes.practice;
final class Workload { static final String UNIT="character"; static final int[] SIZES={2048,4096,8192,16384,32768}; private Workload(){} static Object prepare(int size){ StringBuilder b=new StringBuilder(size); for(int i=0;i<size;i++)b.append((char)('a'+(i%17))); return b.toString(); } static void run(Object state){ if(Solution.reorganize((String)state).isEmpty())throw new IllegalStateException(); } static int units(int size){ return 1; } }

