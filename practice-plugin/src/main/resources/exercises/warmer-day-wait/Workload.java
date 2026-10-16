package com.jinloes.practice;

/** Scaling workload for Warmer Day Wait; n is the number of days in the log. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        int[] temperatures = new int[size];
        for (int i = 0; i < size; i++) {
            temperatures[i] = (int) ((i * 7919L) % 1000);
        }
        return temperatures;
    }

    static void run(Object state) {
        int[] temperatures = (int[]) state;
        int[] days = Solution.daysUntilWarmer(temperatures);
        if (days.length != temperatures.length || days[temperatures.length - 1] != 0) {
            throw new IllegalStateException("Unexpected result");
        }
    }

    static int units(int size) {
        return 1;
    }
}
