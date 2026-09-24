package com.jinloes.practice;

/**
 * Scaling workload for Roman to Integer. A numeral has at most 15 symbols, so the probe converts a
 * growing batch of numerals and reports the cost per numeral, which should stay flat.
 */
final class Workload {
    static final String UNIT = "numeral";
    static final int[] SIZES = {4_096, 8_192, 16_384, 32_768, 65_536};
    private static final String[] NUMERALS = numerals();

    private Workload() {}

    static Object prepare(int size) {
        return new int[]{size};
    }

    /** Cycles through every valid numeral and checks each conversion. */
    static void run(Object state) {
        int size = ((int[]) state)[0];
        for (int i = 0; i < size; i++) {
            int expected = i % NUMERALS.length + 1;
            if (Solution.romanToInt(NUMERALS[expected - 1]) != expected) {
                throw new IllegalStateException("Probe numeral " + NUMERALS[expected - 1] + " is " + expected);
            }
        }
    }

    static int units(int size) {
        return size;
    }

    private static String[] numerals() {
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        String[] numerals = new String[3999];
        for (int value = 1; value <= numerals.length; value++) {
            StringBuilder numeral = new StringBuilder();
            int remaining = value;
            for (int i = 0; i < values.length; i++) {
                while (remaining >= values[i]) {
                    numeral.append(symbols[i]);
                    remaining -= values[i];
                }
            }
            numerals[value - 1] = numeral.toString();
        }
        return numerals;
    }
}
