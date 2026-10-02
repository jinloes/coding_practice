package com.jinloes.practice;

import java.util.Arrays;

/**
 * Scaling workload for Running Median. Input construction happens in prepare, outside the timed region,
 * so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "operation";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    /** The state holds the values to add and the median they must end with. */
    static Object prepare(int size) {
        int[] values = new int[size];
        for (int i = 0; i < size; i++) {
            values[i] = (int) ((i * 2654435761L) % 100003) - 50000;
        }
        int[] sorted = values.clone();
        Arrays.sort(sorted);
        double expected = size % 2 == 1
                ? sorted[size / 2]
                : ((long) sorted[size / 2 - 1] + sorted[size / 2]) / 2.0;
        return new Object[]{values, expected};
    }

    /** Every add is followed by a median request, so both operations must stay cheap. */
    static void run(Object state) {
        Object[] probe = (Object[]) state;
        int[] values = (int[]) probe[0];
        Solution stream = new Solution();
        double last = 0;
        for (int value : values) {
            stream.add(value);
            last = stream.median();
        }
        if (last != (double) probe[1]) {
            throw new IllegalStateException("Probe median was " + last);
        }
    }

    static int units(int size) {
        return 1;
    }
}
