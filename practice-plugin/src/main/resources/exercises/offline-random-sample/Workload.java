package com.jinloes.practice;
import java.util.Random;
final class Workload { static final String UNIT="call"; static final int[] SIZES={4096,8192,16384,32768,65536}; private Workload(){} static Object prepare(int size){ int[] v=new int[size]; for(int i=0;i<size;i++) v[i]=i; return v; } static void run(Object state){ int[] v=(int[])state; Solution.samplePrefix(v,v.length,new Random(1)); } static int units(int size){ return 1; } }
