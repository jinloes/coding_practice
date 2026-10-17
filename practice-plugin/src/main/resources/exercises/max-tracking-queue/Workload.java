package com.jinloes.practice;

final class Workload {
    static final String UNIT = "operation";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private Workload() {}
    static Object prepare(int size) { return new int[]{size}; }
    static void run(Object state) { int size = ((int[]) state)[0]; Solution q = new Solution(); long checksum = 0; for (int i = 0; i < size; i++) { q.enqueue((i * 37) % 1000 - 500); checksum += q.max(); } for (int i = 0; i < size; i++) checksum += q.dequeue(); if (checksum == Long.MIN_VALUE) throw new IllegalStateException("Impossible checksum"); }
    static int units(int size) { return 1; }
}
