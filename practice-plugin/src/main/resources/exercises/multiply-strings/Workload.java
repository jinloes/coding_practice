package com.jinloes.practice;

/** Scaling workload for Multiply Strings. n is the digit count of each factor; both are all nines. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {256, 512, 1024, 2048, 4096};

    private Workload() {}

    static Object prepare(int size) {
        return "9".repeat(size);
    }

    static void run(Object state) {
        String nines = (String) state;
        String product = Solution.multiply(nines, nines);
        if (product.length() != 2 * nines.length() || product.charAt(0) != '9' || product.charAt(product.length() - 1) != '1') {
            throw new IllegalStateException("Wrong product");
        }
    }

    static int units(int size) {
        return 1;
    }
}
