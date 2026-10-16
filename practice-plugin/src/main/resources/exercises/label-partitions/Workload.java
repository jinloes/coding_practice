package com.jinloes.practice;

/** Scaling workload for Label Partitions. n is the string length: 26 blocks of one repeated letter each. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        StringBuilder text = new StringBuilder(size);
        int block = size / 26;
        for (int letter = 0; letter < 26; letter++) {
            for (int i = 0; i < block; i++) {
                text.append((char) ('a' + letter));
            }
        }
        while (text.length() < size) {
            text.append('z');
        }
        return text.toString();
    }

    static void run(Object state) {
        if (Solution.partitionSizes((String) state).size() != 26) {
            throw new IllegalStateException("Expected 26 pieces");
        }
    }

    static int units(int size) {
        return 1;
    }
}
