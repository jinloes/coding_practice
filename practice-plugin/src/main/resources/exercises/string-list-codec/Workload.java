package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

/**
 * Scaling workload for String List Codec. Input construction happens in prepare, outside the timed region,
 * so only the learner's own work is measured.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {500, 1_000, 2_000, 4_000, 8_000};

    private Workload() {}

    /** Short entries, some empty and some containing the characters naive formats use as separators. */
    static Object prepare(int size) {
        String[] samples = {"alpha-".repeat(12), "", "b,c#" .repeat(16), "12#x".repeat(8), "delta-delta".repeat(6)};
        List<String> strings = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            strings.add(samples[i % samples.length]);
        }
        return strings;
    }

    @SuppressWarnings("unchecked")
    static void run(Object state) {
        List<String> strings = (List<String>) state;
        List<String> decoded = Solution.decode(Solution.encode(strings));
        if (!decoded.equals(strings)) {
            throw new IllegalStateException("Round trip did not restore the list");
        }
    }

    static int units(int size) {
        return 1;
    }
}
