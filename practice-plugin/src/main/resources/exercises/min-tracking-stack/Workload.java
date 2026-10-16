package com.jinloes.practice;

/** Scaling workload for Min-Tracking Stack; only the stack operations are timed. */
final class Workload {
    static final String UNIT = "operation";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        return new int[]{size};
    }

    /** Pushes a zigzag sequence, asks for the minimum after each push, then pops everything. */
    static void run(Object state) {
        int size = ((int[]) state)[0];
        Solution stack = new Solution();
        long checksum = 0;
        for (int i = 0; i < size; i++) {
            stack.push((i * 31) % 1000 - 500);
            checksum += stack.min();
        }
        for (int i = 0; i < size; i++) {
            stack.pop();
        }
        if (stack.size() != 0 || checksum > 0) {
            throw new IllegalStateException("Unexpected stack state");
        }
    }

    static int units(int size) {
        return size * 3;
    }
}
