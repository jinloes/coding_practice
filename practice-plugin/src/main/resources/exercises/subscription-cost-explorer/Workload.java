package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

final class Workload {
    static final String UNIT = "run";
    static final int[] SIZES = {16_384, 32_768, 65_536, 131_072, 262_144};

    private Workload() {
    }

    static Object prepare(int size) {
        String[] starts = {
                "2024-11-10", "2025-01-05", "2025-03-15", "2025-06-20", "2025-09-01", "2026-01-01"
        };
        String[] ends = {
                "2025-04-10", "2025-08-25", null, "2025-11-30", null, null
        };
        List<Solution.Subscription> subs = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            int idx = i % starts.length;
            subs.add(new Solution.Subscription("P" + idx, 500L + (i % 50) * 100L, starts[idx], ends[idx]));
        }
        return subs;
    }

    @SuppressWarnings("unchecked")
    static void run(Object state) {
        List<Solution.Subscription> subs = (List<Solution.Subscription>) state;
        long[] report = new Solution().monthlyCostReport(subs, 2025);
        if (report.length != 12 || report[0] < 0) {
            throw new IllegalStateException("Invalid report");
        }
    }

    static int units(int size) {
        return 1;
    }
}
