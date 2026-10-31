package com.jinloes.practice;

/** Scaling workload for Nested Repeat Decoding; the encoded string is built outside the timed region. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private static final String GROUP = "2[ab3[c]]";
    private static final int DECODED_GROUP_LENGTH = 10;

    private Workload() {}

    /** One nested group per eight units of size, each decoding to ten characters. */
    static Object prepare(int size) {
        return GROUP.repeat(size / 8);
    }

    static void run(Object state) {
        String encoded = (String) state;
        int groups = encoded.length() / GROUP.length();
        if (Solution.decode(encoded).length() != groups * DECODED_GROUP_LENGTH) {
            throw new IllegalStateException("Unexpected result");
        }
    }

    static int units(int size) {
        return 1;
    }
}
