package com.jinloes.practice;

final class Workload {
    static final String UNIT = "operation";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private Workload() {}
    static Object prepare(int size) { return new int[]{size}; }
    static void run(Object state) {
        int size = ((int[]) state)[0];
        Solution stack = new Solution();
        long sum = 0;
        for (int i = 0; i < size; i++) stack.push((i * 17) % 1009);
        for (int i = 0; i < size / 2; i++) sum += stack.popMax();
        for (int i = 0; i < size / 2; i++) sum += stack.pop();
        if (sum < 0) throw new IllegalStateException("bad checksum");
    }
    static int units(int size) { return size * 2; }
}
