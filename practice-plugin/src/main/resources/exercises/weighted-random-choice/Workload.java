package com.jinloes.practice;
import java.util.Random;
final class Workload { static final String UNIT="call"; static final int[] SIZES={4096,8192,16384,32768,65536}; private Workload(){} static Object prepare(int size){ int[] v=new int[size]; double[] w=new double[size]; for(int i=0;i<size;i++){ v[i]=i; w[i]=i+1.0; } return new Object[]{v,w}; } static void run(Object state){ Object[] s=(Object[])state; Solution.choose((int[])s[0],(double[])s[1],new Random(1)); } static int units(int size){ return 1; } }
