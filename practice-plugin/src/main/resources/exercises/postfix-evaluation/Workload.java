package com.jinloes.practice;

/** Scaling workload for Postfix Evaluation; the token array is built outside the timed region. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    /** Half the tokens push a 1 and the other half add them up, so the value stack grows to n / 2. */
    static Object prepare(int size) {
        int half = size / 2;
        String[] tokens = new String[2 * half - 1];
        for (int i = 0; i < half; i++) {
            tokens[i] = "1";
        }
        for (int i = half; i < tokens.length; i++) {
            tokens[i] = "+";
        }
        return tokens;
    }

    static void run(Object state) {
        String[] tokens = (String[]) state;
        if (Solution.evaluate(tokens) != (tokens.length + 1) / 2) {
            throw new IllegalStateException("Unexpected result");
        }
    }

    static int units(int size) {
        return 1;
    }
}
