package com.jinloes.practice;

/** Scaling workload for Infix Expression Value; the expression is built outside the timed region. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private static final String CHUNK = "+(2*3-5)";

    private Workload() {}

    /** A sum of parenthesized chunks that each mix precedence levels and evaluate to 1. */
    static Object prepare(int size) {
        return "1" + CHUNK.repeat(size / CHUNK.length());
    }

    static void run(Object state) {
        String expression = (String) state;
        int chunks = (expression.length() - 1) / CHUNK.length();
        if (Solution.evaluate(expression) != chunks + 1) {
            throw new IllegalStateException("Unexpected result");
        }
    }

    static int units(int size) {
        return 1;
    }
}
