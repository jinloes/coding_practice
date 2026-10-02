package com.jinloes.practice;

/**
 * Scaling workload for Grid Paths on a square grid; size is the side length n.
 * The expected count C(2n - 2, n - 1) is computed in prepare, outside the timed region.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {250, 500, 1000, 2000, 4000};
    private static final long MOD = 1_000_000_007L;

    private Workload() {}

    record Grid(int side, int expected) {}

    static Object prepare(int size) {
        return new Grid(size, binomial(2 * size - 2, size - 1));
    }

    static void run(Object state) {
        Grid grid = (Grid) state;
        int result = Solution.count(grid.side(), grid.side());
        if (result != grid.expected()) {
            throw new IllegalStateException("Unexpected result " + result);
        }
    }

    static int units(int size) {
        return 1;
    }

    private static int binomial(int n, int k) {
        long numerator = 1;
        long denominator = 1;
        for (int i = 1; i <= k; i++) {
            numerator = numerator * (n - k + i) % MOD;
            denominator = denominator * i % MOD;
        }
        return (int) (numerator * power(denominator, MOD - 2) % MOD);
    }

    private static long power(long base, long exponent) {
        long result = 1;
        long factor = base % MOD;
        for (long e = exponent; e > 0; e >>= 1) {
            if ((e & 1) == 1) {
                result = result * factor % MOD;
            }
            factor = factor * factor % MOD;
        }
        return result;
    }
}
