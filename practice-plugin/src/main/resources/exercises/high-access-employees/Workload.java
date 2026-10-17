package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {16_384, 32_768, 65_536, 131_072, 262_144};

    private Workload() {
    }

    static Object prepare(int size) {
        List<Solution.Access> logs = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            int minute = (size - i) % (24 * 60);
            logs.add(new Solution.Access("employee", "%02d%02d".formatted(minute / 60, minute % 60)));
        }
        return logs;
    }

    @SuppressWarnings("unchecked")
    static void run(Object state) {
        List<String> result = Solution.highAccessEmployees((List<Solution.Access>) state);
        if (!result.contains("employee")) {
            throw new IllegalStateException("Repeated accesses should qualify the employee");
        }
    }

    static int units(int size) {
        return 1;
    }
}
