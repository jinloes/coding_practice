package com.jinloes.practice;

final class Workload { static final String UNIT="operation"; static final int[] SIZES={4096,8192,16384,32768,65536}; private Workload(){} static Object prepare(int size){return new int[]{size};} static void run(Object state){int size=((int[])state)[0]; Solution t=new Solution(); long sum=0; for(int i=0;i<size;i++) t.increasePopularity(i%1024); for(int i=0;i<size;i++){sum+=t.mostPopular(); t.decreasePopularity(i%1024);} if(sum < -size) throw new IllegalStateException("bad");} static int units(int size){return size*3;} }
