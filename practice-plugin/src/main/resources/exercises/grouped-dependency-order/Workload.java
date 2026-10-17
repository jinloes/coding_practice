package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

final class Workload {
    static final String UNIT = "item";
    static final int[] SIZES = {512, 1024, 2048, 4096, 8192};
    private Workload() {}
    static Object prepare(int size) {
        int[] groups = new int[size];
        List<ArrayList<Integer>> before = new ArrayList<>();
        for (int i = 0; i < size; i++) { groups[i] = i / 8; before.add(new ArrayList<>()); if (i % 8 != 0) before.get(i).add(i - 1); }
        return new Object[]{groups, before.stream().map(list -> (List<Integer>) List.copyOf(list)).toList()};
    }
    static void run(Object state) { Object[] parts = (Object[]) state; int[] groups = (int[]) parts[0]; @SuppressWarnings("unchecked") List<List<Integer>> before = (List<List<Integer>>) parts[1]; if (Solution.sortItems(groups.length, (groups.length + 7) / 8, groups, before).length != groups.length) throw new IllegalStateException("bad order"); }
    static int units(int size) { return 1; }
}

