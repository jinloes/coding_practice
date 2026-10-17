package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4000, 8000, 16000, 32000, 64000};
    private Workload() {}
    static Object prepare(int size) { int[] prices = new int[size]; for (int i = 0; i < size; i++) prices[i] = (i * 37) % 1000; return prices; }
    static void run(Object state) { Solution.maxProfit((int[]) state); }
    static int units(int size) { return 1; }
}
