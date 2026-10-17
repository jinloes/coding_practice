package com.jinloes.practice;

import java.util.Stack;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {256, 512, 1024, 2048, 4096};
    private Workload() {}
    static Object prepare(int size) {
        int[] values = new int[size];
        for (int i = 0; i < size; i++) values[i] = i + 1;
        return values;
    }
    static void run(Object state) {
        int[] values = (int[]) state;
        Stack<Integer> stack = new Stack<>();
        for (int value : values) stack.push(value);
        Solution.sort(stack);
        if (stack.peek() != 1) throw new IllegalStateException("not sorted");
    }
    static int units(int size) { return 1; }
}

