package com.jinloes.practice;

/** Scaling workload for Product Except Self; input construction happens outside the timed region. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {8192, 16384, 32768, 65536, 131072};

    private Workload() {}

    static Object prepare(int size) {
        int[] numbers = new int[size];
        for (int i = 0; i < size; i++) {
            numbers[i] = i % 2 == 0 ? 1 : -1;
        }
        return numbers;
    }

    /** With alternating 1 and -1, result[0] is the product of the other values: -1 to the power size/2. */
    static void run(Object state) {
        int[] result = Solution.productExceptSelf((int[]) state);
        int expected = (result.length / 2) % 2 == 0 ? 1 : -1;
        if (result.length == 0 || result[0] != expected) {
            throw new IllegalStateException("Unexpected product");
        }
    }

    static int units(int size) {
        return 1;
    }
}
