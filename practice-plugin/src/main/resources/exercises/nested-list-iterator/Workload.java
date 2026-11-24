package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

final class Workload {
    static final String UNIT = "run";
    static final int[] SIZES = {16_384, 32_768, 65_536, 131_072, 262_144};

    private Workload() {
    }

    static Object prepare(int size) {
        List<Solution.NestedValue> root = new ArrayList<>(size / 4 + 1);
        for (int i = 0; i < size; i += 4) {
            List<Solution.NestedValue> group = new ArrayList<>(4);
            group.add(Solution.NestedValue.ofInt(i));
            group.add(Solution.NestedValue.ofList(List.of()));
            group.add(Solution.NestedValue.ofList(List.of(
                    Solution.NestedValue.ofInt(i + 1),
                    Solution.NestedValue.ofInt(i + 2))));
            group.add(Solution.NestedValue.ofInt(i + 3));
            root.add(Solution.NestedValue.ofList(group));
        }
        return root;
    }

    @SuppressWarnings("unchecked")
    static void run(Object state) {
        List<Solution.NestedValue> root = (List<Solution.NestedValue>) state;
        Solution iterator = new Solution(root);
        long sum = 0;
        while (iterator.hasNext()) {
            sum += iterator.next();
        }
        if (sum == -1) {
            throw new IllegalStateException("Unreachable");
        }
    }

    static int units(int size) {
        return 1;
    }
}
