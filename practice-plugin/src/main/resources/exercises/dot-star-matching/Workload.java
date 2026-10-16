package com.jinloes.practice;

/** Scaling workload for Dot-Star Matching: a failing match that must fill the whole table. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {256, 512, 1024, 2048, 4096};

    private Workload() {}

    static Object prepare(int size) {
        String text = "a".repeat(size);
        String pattern = "a*".repeat(size / 2) + "b";
        return new String[]{text, pattern};
    }

    static void run(Object state) {
        String[] input = (String[]) state;
        if (Solution.matches(input[0], input[1])) {
            throw new IllegalStateException("The trailing b can never match");
        }
    }

    static int units(int size) {
        return 1;
    }
}
