package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {2_000, 4_000, 8_000, 16_000, 32_000};

    private Workload() {
    }

    static Object prepare(int size) {
        List<String> supported = new ArrayList<>(size);
        StringBuilder header = new StringBuilder(size * 10);
        for (int i = 0; i < size; i++) {
            String tag = "l" + (i / 4) + "-R" + (i % 4);
            supported.add(tag);
        }
        for (int i = size - 1; i >= 0; i--) {
            if (header.length() > 0) {
                header.append(", ");
            }
            header.append(supported.get(i));
        }
        return new Object[] {header.toString(), supported};
    }

    @SuppressWarnings("unchecked")
    static void run(Object state) {
        Object[] data = (Object[]) state;
        String header = (String) data[0];
        List<String> supported = (List<String>) data[1];
        List<String> result = Solution.matchLanguages(header, supported);
        if (result.size() != supported.size()) {
            throw new IllegalStateException("Expected all supported tags to match");
        }
    }

    static int units(int size) {
        return 1;
    }
}
