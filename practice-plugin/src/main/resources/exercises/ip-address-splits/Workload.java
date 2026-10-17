package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {16, 32, 64, 128, 256};

    private Workload() {
    }

    static Object prepare(int size) {
        return "192168011";
    }

    static void run(Object state) {
        String digits = (String) state;
        for (int i = 0; i < 1_000; i++) {
            if (Solution.splitIpAddresses(digits).isEmpty()) {
                throw new IllegalStateException("Probe input should have valid addresses");
            }
        }
    }

    static int units(int size) {
        return 1;
    }
}
