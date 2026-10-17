package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {2000, 4000, 8000, 16000, 32000};
    private Workload() {}
    static Object prepare(int size) { int[] people = new int[size]; for (int i = 0; i < size; i++) people[i] = 40 + (i * 37) % 61; return people; }
    static void run(Object state) { Solution.minBoats((int[]) state, 140); }
    static int units(int size) { return 1; }
}
