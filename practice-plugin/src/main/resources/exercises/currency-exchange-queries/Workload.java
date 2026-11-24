package com.jinloes.practice;

final class Workload {
    static final String UNIT = "run";
    static final int[] SIZES = {4_096, 8_192, 16_384, 32_768, 65_536};

    private Workload() {
    }

    static Object prepare(int size) {
        String[][] pairs = new String[size][2];
        double[] rates = new double[size];
        for (int i = 0; i < size; i++) {
            pairs[i][0] = "C" + i;
            pairs[i][1] = "C" + (i + 1);
            rates[i] = 1.0001;
        }
        String[][] queries = {
                {"C0", "C" + size},
                {"C" + size, "C0"}
        };
        return new Object[]{pairs, rates, queries};
    }

    static void run(Object state) {
        Object[] data = (Object[]) state;
        double[] res = new Solution().evaluateRates(
                (String[][]) data[0], (double[]) data[1], (String[][]) data[2]);
        if (res.length != 2 || res[0] <= 0.0 || res[1] <= 0.0) {
            throw new IllegalStateException("Unexpected exchange result");
        }
    }

    static int units(int size) {
        return 1;
    }
}
