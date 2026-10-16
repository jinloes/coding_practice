package com.jinloes.practice;

/** Scaling workload for Wildcard Parentheses Check. n is the string length: n/2 opens followed by n/2 stars. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        return "(".repeat(size / 2) + "*".repeat(size / 2);
    }

    static void run(Object state) {
        if (!Solution.isBalanced((String) state)) {
            throw new IllegalStateException("Expected a balanced reading");
        }
    }

    static int units(int size) {
        return 1;
    }
}
