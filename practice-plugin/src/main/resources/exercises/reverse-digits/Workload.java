package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        return new int[]{size};
    }

    /** Reverses a spread of generated values; the numbers come from arithmetic so nothing is allocated. */
    static void run(Object state) {
        int size = ((int[]) state)[0];
        if (Solution.reverseDigits(1203) != 3021) {
            throw new IllegalStateException("reverseDigits(1203) must be 3021");
        }
        int checksum = 0;
        for (int i = 0; i < size; i++) {
            checksum += Solution.reverseDigits(i * 0x9E3779B1);
        }
        if (checksum == Integer.MIN_VALUE) {
            throw new IllegalStateException("Unexpected checksum");
        }
    }

    static int units(int size) {
        return size;
    }
}
